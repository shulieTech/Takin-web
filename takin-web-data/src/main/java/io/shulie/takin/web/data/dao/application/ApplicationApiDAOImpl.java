package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.web.data.mapper.mysql.ApplicationApiManageMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationApiManageEntity;
import io.shulie.takin.web.data.param.application.ApplicationApiCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationApiUpdateUserParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/11/4 5:55 下午
 */
@Component
public class ApplicationApiDAOImpl implements ApplicationApiDAO {

    @Autowired
    private ApplicationApiManageMapper apiManageMapper;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Override
    public int insert(ApplicationApiCreateParam param) {
        ApplicationApiManageEntity entity = new ApplicationApiManageEntity();
        entity.setApi(param.getApi());
        entity.setApplicationName(param.getApplicationName());
        entity.setCreateTime(param.getCreateTime());
        entity.setUpdateTime(param.getUpdateTime());
        entity.setMethod(param.getRequestMethod());
        entity.setTenantId(param.getTenantId());
        entity.setUserId(param.getUserId());
        return apiManageMapper.insert(entity);
    }

    @Override
    public int allocationUser(ApplicationApiUpdateUserParam param) {
        if (Objects.isNull(param.getApplicationId())) {
            return 0;
        }
        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(param.getApplicationId());
        if (Objects.isNull(applicationDetailResult)) {
            return 0;
        }
        LambdaQueryWrapper<ApplicationApiManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationApiManageEntity::getApplicationName, applicationDetailResult.getApplicationName());
        queryWrapper.eq(ApplicationApiManageEntity::getTenantId, applicationDetailResult.getTenantId());
        List<ApplicationApiManageEntity> apiManageEntityList = apiManageMapper.selectList(
            queryWrapper);
        if (CollectionUtils.isNotEmpty(apiManageEntityList)) {
            for (ApplicationApiManageEntity entity : apiManageEntityList) {
                entity.setUserId(param.getUserId());
                entity.setApplicationId(param.getApplicationId());
                apiManageMapper.updateById(entity);
            }
        }
        return 0;
    }
}
