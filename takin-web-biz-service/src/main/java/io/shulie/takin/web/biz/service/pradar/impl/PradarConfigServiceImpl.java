package io.shulie.takin.web.biz.service.pradar.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.pradar.PradarZKConfigResponse;
import io.shulie.takin.web.biz.service.pradar.PradarConfigService;
import io.shulie.takin.web.biz.utils.ZkHelper;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.pradar.PradarZkConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.PradarZkConfigMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.param.pradarconfig.PagePradarZkConfigParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PradarConfigServiceImpl implements PradarConfigService {

    @Autowired
    private PradarZkConfigDAO pradarZkConfigDAO;

    @Autowired
    private PradarZkConfigMapper pradarZkConfigMapper;

    @Autowired
    private ZkHelper zkHelper;

    @Override
    public void initZooKeeperData() {
        // 放入zk，只放入系统的
        for (PradarZKConfigResult config : pradarZkConfigDAO.listSystemConfig()) {
            if (!zkHelper.isNodeExists(config.getZkPath())) {
                zkHelper.addPersistentNode(config.getZkPath(), config.getValue());
            }
        }
    }

    @Override
    public PagingList<PradarZKConfigResponse> list(PradarZKConfigQueryRequest queryRequest) {
        PagePradarZkConfigParam param = new PagePradarZkConfigParam();
        param.setTenantIds(Arrays.asList(WebPluginUtils.traceTenantId(), WebPluginUtils.SYS_DEFAULT_TENANT_ID));
        param.setEnvCodeList(Arrays.asList(WebPluginUtils.traceEnvCode(), WebPluginUtils.SYS_DEFAULT_ENV_CODE));
        IPage<PradarZKConfigResult> page = pradarZkConfigDAO.page(param, queryRequest);
        if (page.getTotal() == 0) {
            return PagingList.empty();
        }

        List<PradarZKConfigResult> records = page.getRecords();
        List<PradarZKConfigResponse> responseList = records.stream().map(record -> {
            PradarZKConfigResponse response = new PradarZKConfigResponse();
            response.setId(record.getId());
            response.setZkPath(record.getZkPath());
            response.setRemark(record.getRemark());
            response.setValue(record.getValue());
            response.setModifyTime(DateFormatUtils.format(record.getModifyTime(), "yyyy-MM-dd HH:mm:ss"));
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responseList, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int updateConfig(PradarZKConfigUpdateRequest updateRequest) {
        //try {
        //    PradarConfigCreateParam updateParam = new PradarConfigCreateParam();
        //    BeanUtils.copyProperties(updateRequest, updateParam);
        //    int rows = pradarZkConfigDAO.update(updateParam);
        //    PradarZKConfigResult result = pradarZkConfigDAO.selectById(updateParam.getId());
        //    if (result != null) {
        //        updateNode(result.getZkPath(), result.getValue());
        //    }
        //    return rows;
        //} catch (TakinWebException e) {
        //    throw new TakinWebException(e.getEx(), "更新zk配置异常");
        //}
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int addConfig(PradarZKConfigCreateRequest createRequest) {
        //try {
        //    if (StringUtils.isBlank(createRequest.getZkPath()) ||
        //        StringUtils.isBlank(createRequest.getType()) ||
        //        StringUtils.isBlank(createRequest.getValue())) {
        //        throw new RuntimeException("保存失败，key|type|value不能为空！");
        //    }
        //    if (!createRequest.getZkPath().startsWith("/")) {
        //        throw new RuntimeException("保存失败，key必须以'/'开始！");
        //    }
        //    if (createRequest.getZkPath().length() == 1) {
        //        throw new RuntimeException("保存失败，请输入正确的key路径！");
        //    }
        //    LambdaQueryWrapper<PradarZkConfigEntity> wrapper = new LambdaQueryWrapper<>();
        //    wrapper.eq(PradarZkConfigEntity::getZkPath, createRequest.getZkPath());
        //    Long count = pradarZkConfigMapper.selectCount(wrapper);
        //    if (count > 0) {
        //        throw new RuntimeException(String.format("保存失败，[key:%s] 已被使用", createRequest.getZkPath()));
        //    }
        //    PradarConfigCreateParam addParam = new PradarConfigCreateParam();
        //    BeanUtils.copyProperties(createRequest, addParam);
        //    int rows = pradarZkConfigDAO.insert(addParam);
        //    addNode(addParam.getZkPath(), addParam.getValue());
        //    return rows;
        //} catch (TakinWebException e) {
        //    throw new TakinWebException(e.getEx(), "新增zk配置异常");
        //}
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int deleteConfig(PradarZKConfigDeleteRequest deleteRequest) {

        //try {
        //    PradarConfigCreateParam deleteParam = new PradarConfigCreateParam();
        //    BeanUtils.copyProperties(deleteRequest, deleteParam);
        //    PradarZKConfigResult result = pradarZkConfigDAO.selectById(deleteParam.getId());
        //    if (result != null) {
        //        deleteNode(result.getZkPath());
        //    }
        //    return pradarZkConfigDAO.delete(deleteParam);
        //} catch (TakinWebException e) {
        //    throw new TakinWebException(e.getEx(), "删除zk配置异常");
        //}
    }

}
