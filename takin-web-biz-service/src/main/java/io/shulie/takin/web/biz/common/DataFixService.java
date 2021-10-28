package io.shulie.takin.web.biz.common;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationAttentionParam;
import io.shulie.takin.web.data.mapper.mysql.ApplicationAttentionListMapper;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsManageMapper;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMiddlewareMapper;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMntMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationAttentionListEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据订正接口
 *
 * @author caijianying
 */
@Service
public class DataFixService extends CommonService {

    @Autowired
    ApplicationDsManageMapper dsManageMapper;
    @Autowired
    ApplicationMntMapper applicationMntMapper;
    @Autowired
    ApplicationAttentionListMapper attentionListMapper;
    @Autowired
    ApplicationMiddlewareMapper middlewareMapper;

    @Transactional(rollbackFor = Exception.class)
    public void dataFix() {
        /**
         * 从custoemrId迁移到tenantId的表
         *   t_application_ds_manage
         *   t_application_mnt
         */
        List<String> tableNames = Lists.newArrayList("t_application_ds_manage","t_application_mnt");
        LambdaQueryWrapper<ApplicationMntEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNull(ApplicationMntEntity::getTenantId);
        List<ApplicationMntEntity> appList = applicationMntMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(appList)) {
            appList = Lists.newArrayList();
        }
        appList = appList.stream().filter(t -> Objects.nonNull(t.getCustomerId())).collect(Collectors.toList());
        Map<String, Long> appMap = appList.stream().collect(
            Collectors.toMap(ApplicationMntEntity::getApplicationName, ApplicationMntEntity::getCustomerId));
        //t_application_focus
        ApplicationAttentionParam param = new ApplicationAttentionParam();
        LambdaUpdateWrapper<ApplicationAttentionListEntity> attentionUpdateWrapper;
        List<ApplicationAttentionListEntity> attentionList = attentionListMapper.getAttentionList(param);
        for (ApplicationAttentionListEntity attentionListEntity : attentionList) {
            Long tenantId = appMap.get(attentionListEntity.getApplicationName());
            if (tenantId == null) {
                continue;
            }
            attentionUpdateWrapper = new LambdaUpdateWrapper<>();
            attentionUpdateWrapper.eq(ApplicationAttentionListEntity::getId, attentionListEntity.getId()).set(
                ApplicationAttentionListEntity::getTenantId, tenantId);
            attentionListMapper.update(attentionListEntity, attentionUpdateWrapper);
        }
        //t_application_middleware

    }
}
