package io.shulie.takin.web.data.dao.activity.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.web.data.dao.activity.ActivityCategoryDAO;
import io.shulie.takin.web.data.mapper.mysql.ActivityCategoryMapper;
import io.shulie.takin.web.data.model.mysql.ActivityCategoryEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class ActivityCategoryDAOImpl extends ServiceImpl<ActivityCategoryMapper, ActivityCategoryEntity>
    implements ActivityCategoryDAO, MPUtil<ActivityCategoryEntity> {

    @Override
    public List<ActivityCategoryEntity> list() {
        return baseMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public List<ActivityCategoryEntity> queryChildren(Long parentId) {
        LambdaQueryWrapper<ActivityCategoryEntity> queryWrapper = this.getLambdaQueryWrapper()
            .eq(ActivityCategoryEntity::getParentId, parentId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public boolean exists(Long id) {
        LambdaQueryWrapper<ActivityCategoryEntity> queryWrapper = this.getLambdaQueryWrapper()
            .eq(ActivityCategoryEntity::getId, id);
        return SqlHelper.retBool(baseMapper.selectCount(queryWrapper));
    }

    @Override
    public ActivityCategoryEntity findById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<ActivityCategoryEntity> findByIds(List<Long> ids) {
        LambdaQueryWrapper<ActivityCategoryEntity> queryWrapper = this.getLambdaQueryWrapper()
            .in(!CollectionUtils.isEmpty(ids), ActivityCategoryEntity::getId, ids);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public boolean save(ActivityCategoryEntity entity) {
        return SqlHelper.retBool(baseMapper.insert(entity));
    }

    @Override
    public boolean updateById(ActivityCategoryEntity entity) {
        return SqlHelper.retBool(baseMapper.updateById(entity));
    }

    @Override
    public boolean deleteById(Long id) {
        return SqlHelper.retBool(baseMapper.deleteById(id));
    }

    @Override
    public void updateRelationPath(Long categoryId, String relationPath) {
        ActivityCategoryEntity entity = new ActivityCategoryEntity();
        entity.setId(categoryId);
        entity.setRelationPath(relationPath);
        entity.setGmtUpdate(new Date());
        baseMapper.updateById(entity);
    }

    @Override
    public List<Long> startWithRelationPath(String relationPath) {
        LambdaQueryWrapper<ActivityCategoryEntity> queryWrapper = this.getLambdaQueryWrapper()
            .select(ActivityCategoryEntity::getId)
            .eq(ActivityCategoryEntity::getRelationPath, relationPath)
            .or()
            .likeRight(ActivityCategoryEntity::getRelationPath, ActivityCategoryDAO.completedEndIfNecessary(
                relationPath));
        List<ActivityCategoryEntity> categoryEntities = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(categoryEntities)) {
            return categoryEntities.stream().map(ActivityCategoryEntity::getId).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }
}
