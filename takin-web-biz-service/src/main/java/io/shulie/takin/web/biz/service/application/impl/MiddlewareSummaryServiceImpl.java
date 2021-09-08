package io.shulie.takin.web.biz.service.application.impl;

import java.util.Date;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.pradar.ext.commons.lang3.StringUtils;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.biz.service.application.MiddlewareJarService;
import io.shulie.takin.web.biz.service.application.MiddlewareSummaryService;
import io.shulie.takin.web.data.mapper.mysql.MiddlewareSummaryMapper;
import io.shulie.takin.web.data.model.mysql.MiddlewareJarEntity;
import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiddlewareSummaryServiceImpl extends ServiceImpl<MiddlewareSummaryMapper, MiddlewareSummaryEntity>
    implements MiddlewareSummaryService {

    @Autowired
    private MiddlewareJarService middlewareJarService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void edit(MiddlewareSummaryEntity middlewareSummaryEntity) {
        final MiddlewareSummaryEntity before = this.getById(middlewareSummaryEntity.getId());

        // 修改所有的 中间件类型
        if (!StringUtils.equals(before.getType(), middlewareSummaryEntity.getType())) {
            final UpdateWrapper<MiddlewareJarEntity> updateWrapper = new UpdateWrapper<>();
            final LambdaUpdateWrapper<MiddlewareJarEntity> lambda = updateWrapper.lambda();
            lambda.set(MiddlewareJarEntity::getType, middlewareSummaryEntity.getType());
            lambda.set(MiddlewareJarEntity::getGmtUpdate, new Date());
            lambda.eq(MiddlewareJarEntity::getArtifactId, middlewareSummaryEntity.getArtifactId());
            lambda.eq(MiddlewareJarEntity::getGroupId, middlewareSummaryEntity.getGroupId());
            middlewareJarService.update(updateWrapper);
        }

        middlewareSummaryEntity.setGmtUpdate(new Date());
        this.updateById(middlewareSummaryEntity);

        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        OperationLogContextHolder.addVars("beforeJSON", JSON.toJSONString(before));
        OperationLogContextHolder.addVars("afterJSON", JSON.toJSONString(middlewareSummaryEntity));

    }
}
