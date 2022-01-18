package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.web.data.mapper.mysql.ShadowJobConfigMapper;
import io.shulie.takin.web.data.model.mysql.ShadowJobConfigEntity;
import io.shulie.takin.web.data.param.application.ShadowJobCreateParam;
import io.shulie.takin.web.data.param.application.ShadowJobUpdateUserParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/11/9 9:02 下午
 */
@Component
public class ApplicationShadowJobDAOImpl implements ApplicationShadowJobDAO {

    @Autowired
    private ShadowJobConfigMapper shadowJobConfigMapper;

    @Override
    public int insert(ShadowJobCreateParam param) {
        ShadowJobConfigEntity entity = new ShadowJobConfigEntity();
        BeanUtils.copyProperties(param, entity);
        return shadowJobConfigMapper.insert(entity);
    }

    @Override
    public int allocationUser(ShadowJobUpdateUserParam param) {
        if (Objects.isNull(param.getApplicationId())) {
            return 0;
        }
        LambdaQueryWrapper<ShadowJobConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShadowJobConfigEntity::getApplicationId, param.getApplicationId());
        List<ShadowJobConfigEntity> shadowJobConfigEntityList = shadowJobConfigMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(shadowJobConfigEntityList)) {
            for (ShadowJobConfigEntity entity : shadowJobConfigEntityList) {
                entity.setUserId(param.getUserId());
                shadowJobConfigMapper.updateById(entity);
            }
        }
        return 0;
    }

    @Override
    public Boolean exist(ShadowJobCreateParam param) {
        LambdaQueryWrapper<ShadowJobConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShadowJobConfigEntity::getApplicationId, param.getApplicationId());
        queryWrapper.eq(ShadowJobConfigEntity::getType, param.getType());
        queryWrapper.eq(ShadowJobConfigEntity::getName, param.getName());
        if(param.getId() != null ) {
            queryWrapper.ne(ShadowJobConfigEntity::getId, param.getId());
        }
        queryWrapper.eq(ShadowJobConfigEntity::getIsDeleted, false);
        return shadowJobConfigMapper.selectCount(queryWrapper) > 0;
    }
}
