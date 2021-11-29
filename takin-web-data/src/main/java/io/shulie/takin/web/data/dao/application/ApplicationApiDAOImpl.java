package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.query.ApplicationApiParam;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.ApplicationApiManageMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationApiManageEntity;
import io.shulie.takin.web.data.param.application.ApplicationApiCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationApiQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationApiUpdateUserParam;
import io.shulie.takin.web.data.result.application.ApplicationApiManageResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
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
        entity.setMethod(param.getMethod());
        entity.setTenantId(param.getTenantId());
        entity.setUserId(param.getUserId());
        entity.setIsAgentRegiste(param.getIsAgentRegiste());
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

    @Override
    public int deleteByPrimaryKey(Long id) {
        return apiManageMapper.deleteById(id);
    }

    @Override
    public int insertSelective(ApplicationApiCreateParam record) {
        ApplicationApiManageEntity entity = new ApplicationApiManageEntity();
        BeanUtils.copyProperties(record,entity);
        return apiManageMapper.insertSelective(entity);
    }

    @Override
    public ApplicationApiManageResult selectByPrimaryKey(Long id) {
        ApplicationApiManageEntity entity = apiManageMapper.selectById(id);
        if(entity == null) {
            return null;
        }
        ApplicationApiManageResult  result = new ApplicationApiManageResult();
        BeanUtils.copyProperties(entity,result);
        return result;
    }

    @Override
    public int updateByPrimaryKeySelective(ApplicationApiCreateParam record) {
        ApplicationApiManageEntity entity = new ApplicationApiManageEntity();
        BeanUtils.copyProperties(record,entity);
        return apiManageMapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public int updateByPrimaryKey(ApplicationApiCreateParam record) {
        ApplicationApiManageEntity entity = new ApplicationApiManageEntity();
        BeanUtils.copyProperties(record,entity);
        return apiManageMapper.updateByPrimaryKey(entity);
    }

    @Override
    public List<ApplicationApiManageResult> query() {
        List<ApplicationApiManageEntity> query = apiManageMapper.query();
        if(CollectionUtils.isEmpty(query)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(query,ApplicationApiManageResult.class);
    }

    @Override
    public List<ApplicationApiManageResult> querySimple(ApplicationApiParam apiParam) {
        List<ApplicationApiManageEntity> query = apiManageMapper.querySimple(apiParam);
        if(CollectionUtils.isEmpty(query)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(query,ApplicationApiManageResult.class);
    }

    @Override
    public List<ApplicationApiManageResult> querySimpleWithTenant(ApplicationApiParam apiParam) {
        List<ApplicationApiManageEntity> query = apiManageMapper.querySimpleWithTenant(apiParam);
        if(CollectionUtils.isEmpty(query)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(query,ApplicationApiManageResult.class);
    }

    @Override
    public List<ApplicationApiManageResult> selectBySelective(ApplicationApiQueryParam record, List<Long> userIds) {
        List<ApplicationApiManageEntity> query = apiManageMapper.selectBySelective(record,userIds);
        if(CollectionUtils.isEmpty(query)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(query,ApplicationApiManageResult.class);
    }

    @Override
    public int insertBatch(List<ApplicationApiCreateParam> list) {
        List<ApplicationApiManageEntity> entities = DataTransformUtil.list2list(list,ApplicationApiManageEntity.class);
        return apiManageMapper.insertBatch(entities);
    }

    @Override
    public void deleteByAppName(String appName) {
         apiManageMapper.deleteByAppName(appName);
    }
}
