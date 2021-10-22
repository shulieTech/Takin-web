package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.AppAgentConfigReportDAO;
import io.shulie.takin.web.data.mapper.mysql.AppAgentConfigReportMapper;
import io.shulie.takin.web.data.model.mysql.AppAgentConfigReportEntity;
import io.shulie.takin.web.data.param.application.CreateAppAgentConfigReportParam;
import io.shulie.takin.web.data.param.application.UpdateAppAgentConfigReportParam;
import io.shulie.takin.web.data.result.application.AppAgentConfigReportDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * agent配置上报详情(AppAgentConfigReport)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-09-28 17:27:22
 */
@Service
public class AppAgentConfigReportDAOImpl extends ServiceImpl<AppAgentConfigReportMapper, AppAgentConfigReportEntity>  implements AppAgentConfigReportDAO, MPUtil<AppAgentConfigReportEntity> {


    @Override
    public void batchSave(List<CreateAppAgentConfigReportParam> list) {
        List<AppAgentConfigReportEntity> collect = list.stream()
                .map(param -> Convert.convert(AppAgentConfigReportEntity.class, param))
                .collect(Collectors.toList());
        this.saveBatch(collect);
    }

    @Override
    public void batchUpdate(List<UpdateAppAgentConfigReportParam> list) {
        List<AppAgentConfigReportEntity> collect = list.stream()
                .map(param -> Convert.convert(AppAgentConfigReportEntity.class, param))
                .collect(Collectors.toList());
        this.updateBatchById(collect);
    }

    @Override
    public List<AppAgentConfigReportDetailResult> listByAppId(Long appId) {
        LambdaQueryWrapper<AppAgentConfigReportEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppAgentConfigReportEntity::getApplicationId,appId);
        queryWrapper.eq(AppAgentConfigReportEntity::getIsDeleted,0);

        List<AppAgentConfigReportEntity> list = this.list(queryWrapper);
        return this.convertVoList(list);
    }

    @Override
    public List<AppAgentConfigReportDetailResult> listByBizType(Integer bizType,String appName) {
        LambdaQueryWrapper<AppAgentConfigReportEntity> queryWrapper = this.getCustomerLambdaQueryWrapper();
        queryWrapper.eq(AppAgentConfigReportEntity::getConfigType,bizType);
        if(StringUtils.isNotBlank(appName)){
            queryWrapper.like(AppAgentConfigReportEntity::getApplicationName,appName);
        }
        List<AppAgentConfigReportEntity> list = this.list(queryWrapper);
        return this.convertVoList(list);
    }

    public List<AppAgentConfigReportDetailResult> convertVoList(List<AppAgentConfigReportEntity> list){
        if(CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        return list.stream().map(entity -> Convert.convert(AppAgentConfigReportDetailResult.class,entity)).collect(Collectors.toList());
    }
}

