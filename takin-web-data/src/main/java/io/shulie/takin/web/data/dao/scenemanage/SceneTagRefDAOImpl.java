package io.shulie.takin.web.data.dao.scenemanage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.SceneTagRefMapper;
import io.shulie.takin.web.data.model.mysql.SceneTagRefEntity;
import io.shulie.takin.web.data.param.sceneManage.SceneTagRefInsertParam;
import io.shulie.takin.web.data.param.sceneManage.SceneTagRefQueryParam;
import io.shulie.takin.web.data.result.scenemanage.SceneTagRefResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

/**
 * @author mubai
 * @date 2020-11-30 16:31
 */

@Repository
@Slf4j
public class SceneTagRefDAOImpl implements SceneTagRefDAO {

    @Resource
    private SceneTagRefMapper sceneTagRefMapper;

    @Override
    public void createSceneTagRefBatch(List<SceneTagRefInsertParam> paramList) {
        paramList.stream().forEach(refInsertParam -> {
            createSceneTagRef(refInsertParam);
        });
    }

    @Override
    public void createSceneTagRef(SceneTagRefInsertParam refInsertParam) {
        try {
            SceneTagRefEntity entity = new SceneTagRefEntity();
            BeanUtils.copyProperties(refInsertParam, entity);
            sceneTagRefMapper.insert(entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<SceneTagRefResult> selectBySceneId(Long sceneId) {
        LambdaQueryWrapper<SceneTagRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            SceneTagRefEntity::getSceneId,
            SceneTagRefEntity::getId,
            SceneTagRefEntity::getTagId);
        wrapper.eq(SceneTagRefEntity::getSceneId, sceneId);
        List<SceneTagRefEntity> entities = sceneTagRefMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entityToResult(entities);

    }

    @Override
    public void deleteBySceneId(Long sceneId) {
        sceneTagRefMapper.deleteBySceneId(sceneId);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        sceneTagRefMapper.deleteBatchIds(ids);
    }

    @Override
    public void addSceneTagRef(List<Long> tagIds, Long sceneId) {
        if (CollectionUtils.isNotEmpty(tagIds) && sceneId != null) {
            List<SceneTagRefResult> refResultList = selectBySceneId(sceneId);
            List<Long> existTagIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(refResultList)) {
                existTagIds = refResultList.stream().map(SceneTagRefResult::getTagId).collect(Collectors.toList());
            }
            for (Long tagId : tagIds) {
                if (CollectionUtils.isNotEmpty(existTagIds) && existTagIds.contains(tagId)) {
                    return;
                }
                SceneTagRefEntity sceneTagRefEntity = new SceneTagRefEntity();
                sceneTagRefEntity.setTagId(tagId);
                sceneTagRefEntity.setSceneId(sceneId);
                sceneTagRefMapper.insert(sceneTagRefEntity);
            }
        }
    }

    @Override
    public List<SceneTagRefResult> selectBySceneIds(List<Long> sceneIds) {
        LambdaQueryWrapper<SceneTagRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            SceneTagRefEntity::getSceneId,
            SceneTagRefEntity::getId,
            SceneTagRefEntity::getTagId);
        wrapper.in(SceneTagRefEntity::getSceneId, sceneIds);
        List<SceneTagRefEntity> entities = sceneTagRefMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entityToResult(entities);
    }

    @Override
    public List<SceneTagRefResult> selectByExample(SceneTagRefQueryParam queryParam) {
        LambdaQueryWrapper<SceneTagRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            SceneTagRefEntity::getSceneId,
            SceneTagRefEntity::getId,
            SceneTagRefEntity::getTagId);
        wrapper.in(SceneTagRefEntity::getTagId, queryParam.getTagIds());
        List<SceneTagRefEntity> entities = sceneTagRefMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entityToResult(entities);
    }

    List<SceneTagRefResult> entityToResult(List<SceneTagRefEntity> sourceList) {
        List<SceneTagRefResult> refResultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceList)) {
            return refResultList;
        }
        sourceList.stream().forEach(sceneTagRefEntity -> {
            refResultList.add(entityToResult(sceneTagRefEntity));
        });
        return refResultList;

    }

    SceneTagRefResult entityToResult(SceneTagRefEntity source) {
        SceneTagRefResult result = new SceneTagRefResult();
        BeanUtils.copyProperties(source, result);
        return result;
    }

}
