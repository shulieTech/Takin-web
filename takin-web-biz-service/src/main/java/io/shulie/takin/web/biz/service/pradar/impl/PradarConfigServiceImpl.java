package io.shulie.takin.web.biz.service.pradar.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.pradar.PradarZKConfigResponse;
import io.shulie.takin.web.biz.service.pradar.PradarConfigService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.pradar.PradarZkConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.PradarZkConfigMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigQueryParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class PradarConfigServiceImpl implements PradarConfigService {

    List<PradarZKConfig> pradarZooKeeperConfigList = Arrays.asList(
        new PradarZKConfig(1, "/pradar/config/rt/hdfsDisable", PradarZKDataType.Boolean, "false",
            "hdfs开关配置，控制数据是否不写入hdfs"),
        new PradarZKConfig(2, "/pradar/config/rt/hdfsSampling", PradarZKDataType.Int, "1",
            "日志原文写入hdfs采样率配置，代表每多少条数据采样1条"),
        new PradarZKConfig(3, "/pradar/config/rt/monitakinDisable", PradarZKDataType.Boolean, "false",
            "基础信息处理开关，控制压测指标信息是否不处理"),
        new PradarZKConfig(4, "/pradar/config/rt/clickhouseDisable", PradarZKDataType.Boolean, "false",
            "日志原文写入clickhouse开关，控制日志原文是否不写入clickhouse"),
        new PradarZKConfig(5, "/pradar/config/rt/clickhouseSampling", PradarZKDataType.Int, "1",
            "日志原文写入clickhouse采样率，代表每多少条数据采样1条"),
        new PradarZKConfig(6, "/config/log/trace/simpling", PradarZKDataType.Int, "1",
            "全局日志采样率"),
        new PradarZKConfig(7, "/pradar/config/rt/e2eMetricsDisable", PradarZKDataType.Boolean, "false",
            "E2E流量计算开关,控制流量是否不计算"),
        new PradarZKConfig(8, "/pradar/config/ptl/fileEnable", PradarZKDataType.Boolean, "true",
            "是否输出ptl文件"),
        new PradarZKConfig(9, "/pradar/config/ptl/errorOnly", PradarZKDataType.Boolean, "false",
            "ptl文件是否只输出异常信息"),
        new PradarZKConfig(10, "/pradar/config/ptl/timeoutOnly", PradarZKDataType.Boolean, "false",
            "ptl是否只输出超过固定接口调用时长的日志"),
        new PradarZKConfig(11, "/pradar/config/ptl/timeoutThreshold", PradarZKDataType.Int, "300",
            "接口调用时长阈值"),
        new PradarZKConfig(12, "/pradar/config/ptl/cutoff", PradarZKDataType.Boolean, "false",
            "ptl报文日志是否截断"),
        new PradarZKConfig(13, "/config/engine/local/mount/sceneIds", PradarZKDataType.String, "",
            "本地挂载的场景ID, 多个以逗号分隔")
    );

    @Autowired
    private Environment environment;

    private CuratorFramework client;

    @Autowired
    private PradarZkConfigDAO pradarZkConfigDAO;

    @Autowired
    private PradarZkConfigMapper pradarZkConfigMapper;

    @PostConstruct
    public void init() {
        String zkAddr = environment.getProperty("takin.config.zk.addr");
        if (StringUtils.isBlank(zkAddr)) {
            throw new RuntimeException("配置中心zk地址没有填写，请核对校验`takin.config.zk.addr`");
        }
        int timeout = 3000;

        try {
            String timeoutString = environment.getProperty("takin.config.zk.timeout");
            if (timeoutString != null) {
                timeout = Integer.parseInt(timeoutString);
            }
        } catch (Exception e) {
            // ignore
        }
        client = CuratorFrameworkFactory
            .builder()
            .connectString(zkAddr)
            .sessionTimeoutMs(timeout)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
        client.start();
    }

    @Override
    public List<PradarZKConfigResponse> getConfigList() {
        Stat stat = new Stat();
        return pradarZooKeeperConfigList.stream().map(config -> new PradarZKConfigResponse(
            config.getId(),
            config.getZkPath(),
            config.getType().type,
            getNode(stat, config.getZkPath()),
            config.getRemark(),
            stat.getCtime(),
            stat.getMtime()
        )).collect(Collectors.toList());
    }

    @Override
    public PagingList<PradarZKConfigResponse> list(PradarZKConfigQueryRequest queryRequest) {
        Stat stat = new Stat();
        PradarConfigQueryParam queryParam = new PradarConfigQueryParam();
        BeanUtils.copyProperties(queryRequest, queryParam);
        PagingList<PradarZKConfigResult> pagingList = pradarZkConfigDAO.selectPage(queryParam);
        List<PradarZKConfigResponse> responseList = pagingList.getList().stream()
            .map(config -> new PradarZKConfigResponse(
                config.getId().intValue(),
                config.getZkPath(),
                config.getType(),
                getNode(stat, config.getZkPath()),
                config.getRemark(),
                stat.getCtime(),
                stat.getMtime()
            )).collect(Collectors.toList());
        return PagingList.of(responseList, pagingList.getTotal());
    }

    @Override
    public void initZooKeeperData() {
        //pradarZKConfigList.forEach(pradarZKConfig -> {
        //    if (!checkNodeExists(pradarZKConfig.zkPath)) {
        //        addNode(pradarZKConfig.zkPath, pradarZKConfig.defaultValue);
        //    }
        //});
        pradarZkConfigDAO.selectList().forEach(config -> {
            if (!checkNodeExists(config.getZkPath())) {
                addNode(config.getZkPath(), config.getValue());
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int updateConfig(PradarZKConfigUpdateRequest updateRequest) {
        //try {
        //    pradarZKConfigList.stream().filter(pradarZKConfig -> id == pradarZKConfig.id).forEach(pradarZKConfig -> {
        //        updateNode(pradarZKConfig.zkPath, value);
        //    });
        //    return true;
        //} catch (Exception e) {
        //    return false;
        //}
        try {
            PradarConfigCreateParam updateParam = new PradarConfigCreateParam();
            BeanUtils.copyProperties(updateRequest, updateParam);
            int rows = pradarZkConfigDAO.update(updateParam);
            PradarZKConfigResult result = pradarZkConfigDAO.selectById(updateParam.getId());
            if (result != null) {
                updateNode(result.getZkPath(), result.getValue());
            }
            return rows;
        } catch (TakinWebException e) {
            throw new TakinWebException(e.getEx(), "更新zk配置异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int addConfig(PradarZKConfigCreateRequest createRequest) {
        try {
            if (StringUtils.isBlank(createRequest.getZkPath()) ||
                StringUtils.isBlank(createRequest.getType()) ||
                StringUtils.isBlank(createRequest.getValue())) {
                throw new RuntimeException("保存失败，key|type|value不能为空！");
            }
            if (!createRequest.getZkPath().startsWith("/")) {
                throw new RuntimeException("保存失败，key必须以'/'开始！");
            }
            if (createRequest.getZkPath().length() == 1) {
                throw new RuntimeException("保存失败，请输入正确的key路径！");
            }
            LambdaQueryWrapper<PradarZkConfigEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PradarZkConfigEntity::getZkPath, createRequest.getZkPath());
            Integer count = pradarZkConfigMapper.selectCount(wrapper);
            if (count > 0) {
                throw new RuntimeException(String.format("保存失败，[key:%s] 已被使用", createRequest.getZkPath()));
            }
            PradarConfigCreateParam addParam = new PradarConfigCreateParam();
            BeanUtils.copyProperties(createRequest, addParam);
            int rows = pradarZkConfigDAO.insert(addParam);
            addNode(addParam.getZkPath(), addParam.getValue());
            return rows;
        } catch (TakinWebException e) {
            throw new TakinWebException(e.getEx(), "新增zk配置异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int deleteConfig(PradarZKConfigDeleteRequest deleteRequest) {
        try {
            PradarConfigCreateParam deleteParam = new PradarConfigCreateParam();
            BeanUtils.copyProperties(deleteRequest, deleteParam);
            PradarZKConfigResult result = pradarZkConfigDAO.selectById(deleteParam.getId());
            if (result != null) {
                deleteNode(result.getZkPath());
            }
            return pradarZkConfigDAO.delete(deleteParam);
        } catch (TakinWebException e) {
            throw new TakinWebException(e.getEx(), "删除zk配置异常");
        }
    }

    private void addNode(String path, String data) {
        try {
            client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path, data.getBytes());
        } catch (Exception e) {
            log.error("创建zk数据节点失败;path={},data={}", path, data, e);
        }
    }

    private String getNode(Stat stat, String path) {
        if (!checkNodeExists(path)) {
            return null;
        }
        byte[] bytes = new byte[0];
        try {
            bytes = client.getData().storingStatIn(stat).forPath(path);
        } catch (Exception e) {
            log.error("读取zk数据节点失败;path={}", path, e);
        }
        return new String(bytes);
    }

    private boolean checkNodeExists(String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            log.error("判断数据节点是否存在失败;path={}", path, e);
        }
        return false;
    }

    private void updateNode(String path, String data) {
        try {
            client.setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            log.error("更新zk数据节点失败;path={},data={}", path, data, e);
        }
    }

    private void deleteNode(String path) {
        try {
            // 递归删除节点
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            log.error("删除zk数据节点失败;path={}", path, e);
        }
    }

    private enum PradarZKDataType {
        /**
         * ZK数据类型
         */
        Int("Int"),
        String("String"),
        Boolean("Boolean");

        String type;

        PradarZKDataType(String type) {
            this.type = type;
        }
    }

    @Data
    private static class PradarZKConfig {
        int id;
        String zkPath;
        PradarZKDataType type;
        String defaultValue;
        String remark;

        PradarZKConfig(int id, String zkPath, PradarZKDataType type, String defaultValue, String remark) {
            this.id = id;
            this.zkPath = zkPath;
            this.type = type;
            this.defaultValue = defaultValue;
            this.remark = remark;
        }
    }
}
