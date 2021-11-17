package io.shulie.takin.web.biz.service.pradar.impl;

import java.util.Objects;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZkConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.pradar.PradarZKConfigResponse;
import io.shulie.takin.web.biz.service.pradar.PradarConfigService;
import io.shulie.takin.web.biz.utils.ZkHelper;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.pradar.PradarZkConfigDAO;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZkConfigResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Slf4j
@Component
public class PradarConfigServiceImpl implements PradarConfigService {

    @Autowired
    private PradarZkConfigDAO pradarZkConfigDAO;

    @Autowired
    private ZkHelper zkHelper;

    @Override
    public void initZooKeeperData() {
        // 放入zk，只放入系统的
        for (PradarZkConfigResult config : pradarZkConfigDAO.listSystemConfig()) {
            if (!zkHelper.isNodeExists(config.getZkPath())) {
                zkHelper.addPersistentNode(config.getZkPath(), config.getValue());
            }
        }
    }

    @Override
    public PagingList<PradarZKConfigResponse> page(PradarZKConfigQueryRequest queryRequest) {
        PagingList<PradarZkConfigResult> page = pradarZkConfigDAO.page(WebPluginUtils.SYS_DEFAULT_TENANT_ID,
            WebPluginUtils.SYS_DEFAULT_ENV_CODE, queryRequest);
        if (page.getTotal() == 0) {
            return PagingList.empty();
        }
        return PagingList.of(CommonUtil.list2list(page.getList(), PradarZKConfigResponse.class), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateConfig(PradarZkConfigUpdateRequest updateRequest) {
        PradarConfigCreateParam updateParam = BeanUtil.copyProperties(updateRequest, PradarConfigCreateParam.class);
        PradarZkConfigResult config = pradarZkConfigDAO.getById(updateRequest.getId());
        Assert.notNull(config, "配置不存在！");

        // 更新db， 更新zk， 只更新system的
        if (pradarZkConfigDAO.updateOnlySystem(updateParam)
            && Objects.equals(WebPluginUtils.SYS_DEFAULT_TENANT_ID, config.getTenantId())) {
            zkHelper.updateNode(config.getZkPath(), updateRequest.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addConfig(PradarZKConfigCreateRequest createRequest) {
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
    public void deleteConfig(PradarZKConfigDeleteRequest deleteRequest) {
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
