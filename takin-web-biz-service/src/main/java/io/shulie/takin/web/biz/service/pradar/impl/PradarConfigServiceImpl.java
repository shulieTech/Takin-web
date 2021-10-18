package io.shulie.takin.web.biz.service.pradar.impl;

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
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.pradar.PradarZkConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.PradarZkConfigMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigQueryParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PradarConfigServiceImpl implements PradarConfigService {

    private String zkAddr;

    @Value("${takin.config.zk.timeout: 3000}")
    private Integer timeout;

    private CuratorFramework client;

    @Autowired
    private PradarZkConfigDAO pradarZkConfigDAO;

    @Autowired
    private PradarZkConfigMapper pradarZkConfigMapper;

    @PostConstruct
    public void init() {
        zkAddr = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_CONFIG_ZOOKEEPER_ADDRESS);
        client = CuratorFrameworkFactory
            .builder()
            .connectString(zkAddr)
            .sessionTimeoutMs(timeout)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
        client.start();
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
        pradarZkConfigDAO.selectList().forEach(config -> {
            if (!checkNodeExists(config.getZkPath())) {
                addNode(config.getZkPath(), config.getValue());
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int updateConfig(PradarZKConfigUpdateRequest updateRequest) {
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
                .forPath(CommonUtil.getZkTenantAndEnvPath(path), data.getBytes());
        } catch (Exception e) {
            log.error("创建zk数据节点失败;path={},data={}", CommonUtil.getZkTenantAndEnvPath(path), data, e);
        }
    }

    private String getNode(Stat stat, String path) {
        if (!checkNodeExists(path)) {
            return null;
        }
        byte[] bytes = new byte[0];
        try {
            bytes = client.getData().storingStatIn(stat).forPath(CommonUtil.getZkTenantAndEnvPath(path));
        } catch (Exception e) {
            log.error("读取zk数据节点失败;path={}", CommonUtil.getZkTenantAndEnvPath(path), e);
        }
        return new String(bytes);
    }

    private boolean checkNodeExists(String path) {
        try {
            return client.checkExists().forPath(CommonUtil.getZkTenantAndEnvPath(path)) != null;
        } catch (Exception e) {
            log.error("判断数据节点是否存在失败;path={}", CommonUtil.getZkTenantAndEnvPath(path), e);
        }
        return false;
    }

    private void updateNode(String path, String data) {
        try {
            client.setData().forPath(CommonUtil.getZkTenantAndEnvPath(path), data.getBytes());
        } catch (Exception e) {
            log.error("更新zk数据节点失败;path={},data={}", CommonUtil.getZkTenantAndEnvPath(path), data, e);
        }
    }

    private void deleteNode(String path) {
        try {
            // 递归删除节点
            client.delete().deletingChildrenIfNeeded().forPath(CommonUtil.getZkTenantAndEnvPath(path));
        } catch (Exception e) {
            log.error("删除zk数据节点失败;path={}", CommonUtil.getZkTenantAndEnvPath(path), e);
        }
    }

}
