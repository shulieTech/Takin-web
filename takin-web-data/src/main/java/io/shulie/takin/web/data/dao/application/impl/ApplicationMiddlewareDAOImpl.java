package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.dao.application.ApplicationMiddlewareDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMiddlewareMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationMiddlewareEntity;
import io.shulie.takin.web.data.param.application.CreateApplicationMiddlewareParam;
import io.shulie.takin.web.data.param.application.PageApplicationMiddlewareParam;
import io.shulie.takin.web.data.param.application.UpdateApplicationMiddlewareParam;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareListResult;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareStatusAboutCountResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 应用中间件(ApplicationMiddleware)表数据库 dao 层实现
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:37
 */
@Service
public class ApplicationMiddlewareDAOImpl implements ApplicationMiddlewareDAO, MPUtil<ApplicationMiddlewareEntity> {

    @Autowired
    private ApplicationMiddlewareMapper applicationMiddlewareMapper;

    @Override
    public IPage<ApplicationMiddlewareListResult> page(PageApplicationMiddlewareParam param) {
        IPage<ApplicationMiddlewareEntity> entityPage =
            applicationMiddlewareMapper.selectPage(this.setPage(param),
                this.getLambdaQueryWrapper().eq(ApplicationMiddlewareEntity::getApplicationId, param.getApplicationId())
                    .eq(param.getStatus() != null, ApplicationMiddlewareEntity::getStatus, param.getStatus())
                    .and(!StringUtils.isEmpty(param.getKeywords()),
                        wrapper -> wrapper.like(ApplicationMiddlewareEntity::getGroupId, param.getKeywords()).or()
                            .like(ApplicationMiddlewareEntity::getArtifactId, param.getKeywords())));

        IPage<ApplicationMiddlewareListResult> resultPage = new Page<>();
        BeanUtils.copyProperties(entityPage, resultPage);
        resultPage.setRecords(DataTransformUtil.list2list(entityPage.getRecords(), ApplicationMiddlewareListResult.class));
        return resultPage;
    }

    @Override
    public Long countByApplicationIdAndStatus(Long applicationId, Integer status) {
        return applicationMiddlewareMapper.selectCount(this.getLambdaQueryWrapper()
            .eq(ApplicationMiddlewareEntity::getApplicationId, applicationId)
            .eq(status != null, ApplicationMiddlewareEntity::getStatus, status));
    }

    @Override
    public boolean updateBatchById(List<UpdateApplicationMiddlewareParam> updateParamList) {
        List<ApplicationMiddlewareEntity> applicationMiddlewares = DataTransformUtil.list2list(updateParamList,
            ApplicationMiddlewareEntity.class);
        return applicationMiddlewares.stream().allMatch(applicationMiddleware ->
            SqlHelper.retBool(applicationMiddlewareMapper.updateById(applicationMiddleware)));
    }

    @Override
    public boolean removeByApplicationId(Long applicationId) {
        return SqlHelper.retBool(applicationMiddlewareMapper.delete(this.getLambdaQueryWrapper()
            .eq(ApplicationMiddlewareEntity::getApplicationId, applicationId)));
    }

    @Override
    public boolean insertBatch(List<CreateApplicationMiddlewareParam> createApplicationMiddlewareParamList) {
        List<ApplicationMiddlewareEntity> applicationMiddlewareList =
            DataTransformUtil.list2list(createApplicationMiddlewareParamList, ApplicationMiddlewareEntity.class);
        return SqlHelper.retBool(applicationMiddlewareMapper.insertBatch(applicationMiddlewareList));
    }

    @Override
    public List<ApplicationMiddlewareListResult> listByApplicationId(Long applicationId) {
        List<ApplicationMiddlewareEntity> applicationMiddlewares = applicationMiddlewareMapper.selectList(
            this.getLambdaQueryWrapper().eq(ApplicationMiddlewareEntity::getApplicationId, applicationId));
        return DataTransformUtil.list2list(applicationMiddlewares, ApplicationMiddlewareListResult.class);
    }

    @Override
    public List<ApplicationMiddlewareStatusAboutCountResult> listCountByApplicationIdAndStatusAndGroupByStatus(Long applicationId, List<Integer> statusList) {
        return applicationMiddlewareMapper.selectStatusCountListByApplicationIdAndStatusAndGroupByStatus(applicationId,
            statusList);
    }

    @Override
    public List<ApplicationMiddlewareStatusAboutCountResult> listStatusCountByAndGroupByApplicationNameListAndStatus(
        List<Long> applicationIds, List<Integer> statusList) {
        return applicationMiddlewareMapper.selectStatusCountByAndGroupByApplicationNameListAndStatusList(applicationIds, statusList);
    }

}

