package io.shulie.takin.web.biz.service.pradar.impl;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZkConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZkConfigDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZkConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.pradar.PradarZKConfigResponse;
import io.shulie.takin.web.biz.service.pradar.PradarConfigService;
import io.shulie.takin.web.biz.utils.ZkHelper;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.util.DataTransformUtil;
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
        for (PradarZkConfigResult config : pradarZkConfigDAO.list()) {
            this.processZk(config.getZkPath(), config.getValue());
        }
    }

    @Override
    public PagingList<PradarZKConfigResponse> page(PradarZKConfigQueryRequest queryRequest) {
        PagingList<PradarZkConfigResult> page = pradarZkConfigDAO.page(WebPluginUtils.SYS_DEFAULT_TENANT_ID,
            WebPluginUtils.SYS_DEFAULT_ENV_CODE, queryRequest);
        if (page.getTotal() == 0) {
            return PagingList.empty();
        }
        return PagingList.of(DataTransformUtil.list2list(page.getList(), PradarZKConfigResponse.class), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateConfig(PradarZkConfigUpdateRequest updateRequest) {
        PradarConfigCreateParam updateParam = BeanUtil.copyProperties(updateRequest, PradarConfigCreateParam.class);
        PradarZkConfigResult config = pradarZkConfigDAO.getById(updateRequest.getId());
        Assert.notNull(config, "配置不存在！");
        if (pradarZkConfigDAO.update(updateParam)) {
            this.processZk(config.getZkPath(), updateRequest.getValue());

            // 记录日志
            OperationLogContextHolder.addVars(BizOpConstants.Vars.CONFIG_NAME, config.getZkPath());
            OperationLogContextHolder.addVars(BizOpConstants.Vars.CONFIG_VALUE,updateRequest.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addConfig(PradarZkConfigCreateRequest createRequest) {
        String zkPath = createRequest.getZkPath();
        Assert.isTrue(zkPath.startsWith("/"), "zkPath必须以'/'开头");
        // 判断path是否存在
        PradarZkConfigResult pradarZkConfig = pradarZkConfigDAO.getByZkPath(zkPath);
        Assert.isNull(pradarZkConfig, "zkPath已存在！");
        PradarConfigCreateParam createParam = BeanUtil.copyProperties(createRequest, PradarConfigCreateParam.class);
        if (pradarZkConfigDAO.insert(createParam)) {
            // 如果zk存在, 更新值
            this.processZk(zkPath, createRequest.getValue());

            // 记录日志
            OperationLogContextHolder.addVars(BizOpConstants.Vars.CONFIG_NAME,createRequest.getZkPath());
            OperationLogContextHolder.addVars(BizOpConstants.Vars.CONFIG_VALUE,createRequest.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteConfig(PradarZkConfigDeleteRequest deleteRequest) {
        PradarZkConfigResult config = pradarZkConfigDAO.getById(deleteRequest.getId());
        Assert.notNull(config, "配置不存在！");
        if (pradarZkConfigDAO.deleteById(deleteRequest.getId())) {
            zkHelper.deleteNode(config.getZkPath());
            // 记录日志
            OperationLogContextHolder.addVars(BizOpConstants.Vars.CONFIG_NAME,config.getZkPath());
        }
    }

    /**
     * 处理 zk
     *
     * @param zkPath zk 路径
     * @param value 值
     */
    private void processZk(String zkPath, String value) {
        // 如果zk存在, 更新值
        if (zkHelper.isNodeExists(zkPath)) {
            zkHelper.updateNode(zkPath, value);
        } else {
            // 不存在, 插入
            zkHelper.addPersistentNode(zkPath, value);
        }
    }

}
