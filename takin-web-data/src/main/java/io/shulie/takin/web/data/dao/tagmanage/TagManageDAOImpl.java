package io.shulie.takin.web.data.dao.tagmanage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.TagManageMapper;
import io.shulie.takin.web.data.model.mysql.TagManageEntity;
import io.shulie.takin.web.data.param.tagmanage.TagManageParam;
import io.shulie.takin.web.data.result.tagmanage.TagManageResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Component
public class TagManageDAOImpl implements TagManageDAO {

    @Autowired
    private TagManageMapper tagManageMapper;

    @Override
    public List<TagManageResult> selectAllScript() {
        LambdaQueryWrapper<TagManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            TagManageEntity::getId,
            TagManageEntity::getTagName
        );
        wrapper.eq(TagManageEntity::getTagStatus, 0);
        wrapper.eq(TagManageEntity::getTagType, 0);
        List<TagManageEntity> tagManageEntities = tagManageMapper.selectList(wrapper);
        return entityToResult(tagManageEntities);

    }

    private List<TagManageEntity> getByTagNameAndTagType(String tagName,Integer type) {
        LambdaQueryWrapper<TagManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            TagManageEntity::getId,
            TagManageEntity::getTagName
        );
        wrapper.eq(TagManageEntity::getTagName, tagName);
        if (type!=null){
            wrapper.eq(TagManageEntity::getTagType,type);
        }
        List<TagManageEntity> tagManageEntities = tagManageMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(tagManageEntities)) {
            return tagManageEntities;
        }
        return null;
    }

    @Override
    public List<Long> addScriptTags(List<TagManageParam> tagManageParams,Integer type) {
        List<Long> tagManageIds = new ArrayList<>();
        for (TagManageParam tagManageParam : tagManageParams) {
            List<TagManageEntity> tagManageList = getByTagNameAndTagType(tagManageParam.getTagName(),type);
            if (CollectionUtils.isNotEmpty(tagManageList)) {
                tagManageIds.add(tagManageList.get(0).getId());
                continue;
            }
            TagManageEntity tagManageEntity = new TagManageEntity();
            BeanUtils.copyProperties(tagManageParam, tagManageEntity);
            tagManageMapper.insert(tagManageEntity);
            tagManageIds.add(tagManageEntity.getId());
        }
        return tagManageIds;
    }

    @Override
    public List<TagManageResult> selectDataSourceTags() {
        LambdaQueryWrapper<TagManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            TagManageEntity::getId,
            TagManageEntity::getTagName
        );
        wrapper.eq(TagManageEntity::getTagStatus, 0);
        wrapper.eq(TagManageEntity::getTagType, 1);
        List<TagManageEntity> tagManageEntities = tagManageMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(tagManageEntities)) {
            return tagManageEntities.stream().map(tagManageEntity -> {
                TagManageResult tagManageResult = new TagManageResult();
                tagManageResult.setId(tagManageEntity.getId());
                tagManageResult.setTagName(tagManageEntity.getTagName());
                return tagManageResult;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<TagManageResult> selectScriptTagsByIds(List<Long> tagIds) {
        LambdaQueryWrapper<TagManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            TagManageEntity::getId,
            TagManageEntity::getTagName
        );
        wrapper.eq(TagManageEntity::getTagStatus, 0);
        wrapper.eq(TagManageEntity::getTagType, 0);
        wrapper.in(TagManageEntity::getId, tagIds);
        List<TagManageEntity> tagManageEntities = tagManageMapper.selectList(wrapper);
        return entityToResult(tagManageEntities);
    }

    @Override
    public List<TagManageResult> selectTagByType(Integer type) {
        if (type == null) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<TagManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            TagManageEntity::getId,
            TagManageEntity::getTagName
        );
        wrapper.eq(TagManageEntity::getTagStatus, 0);
        wrapper.eq(TagManageEntity::getTagType, type);
        List<TagManageEntity> tagManageEntities = tagManageMapper.selectList(wrapper);
        return entityToResult(tagManageEntities);
    }

    List<TagManageResult> entityToResult(List<TagManageEntity> tagManageEntities) {
        if (CollectionUtils.isEmpty(tagManageEntities)) {
            return Lists.newArrayList();
        }
        return tagManageEntities.stream().map(tagManageEntity -> {
            TagManageResult tagManageResult = new TagManageResult();
            tagManageResult.setId(tagManageEntity.getId());
            tagManageResult.setTagName(tagManageEntity.getTagName());
            return tagManageResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TagManageResult> selectDataSourceTagsByIds(List<Long> tagIds) {
        LambdaQueryWrapper<TagManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TagManageEntity::getId, tagIds);
        queryWrapper.eq(TagManageEntity::getTagStatus, 0);
        List<TagManageEntity> tagManageEntityList = tagManageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(tagManageEntityList)) {
            return Collections.emptyList();
        }
        return tagManageEntityList.stream().map(tagManageEntity -> {
            TagManageResult tagManageResult = new TagManageResult();
            tagManageResult.setId(tagManageEntity.getId());
            tagManageResult.setTagName(tagManageEntity.getTagName());
            return tagManageResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Long> addDatasourceTags(List<TagManageParam> tagManageParams) {
        List<Long> tagManageIds = new ArrayList<>();
        for (TagManageParam tagManageParam : tagManageParams) {
            List<TagManageEntity> tagManageList = getTagManageEntityByTagName(tagManageParam.getTagName());
            if (CollectionUtils.isNotEmpty(tagManageList)) {
                tagManageIds.add(tagManageList.get(0).getId());
                continue;
            }
            TagManageEntity tagManageEntity = new TagManageEntity();
            BeanUtils.copyProperties(tagManageParam, tagManageEntity);
            tagManageMapper.insert(tagManageEntity);
            tagManageIds.add(tagManageEntity.getId());
        }
        return tagManageIds;
    }

    private List<TagManageEntity> getTagManageEntityByTagName(String tagName) {
        LambdaQueryWrapper<TagManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            TagManageEntity::getId,
            TagManageEntity::getTagName
        );
        wrapper.eq(TagManageEntity::getTagName, tagName);
        List<TagManageEntity> tagManageEntities = tagManageMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(tagManageEntities)) {
            return tagManageEntities;
        }
        return null;
    }

    /**
     * 新增tag
     *
     * @param tagManageParams
     * @return
     */
    @Override
    public List<TagManageResult> addTags(List<TagManageParam> tagManageParams) {
        List<TagManageResult> tags = new ArrayList<>();
        for (TagManageParam tagManageParam : tagManageParams) {
            TagManageResult result = new TagManageResult();
            result.setTagName(tagManageParam.getTagName());
            List<TagManageEntity> tagManageList = getByTagNameAndTagType(tagManageParam.getTagName(),tagManageParam.getTagType());
            if (CollectionUtils.isNotEmpty(tagManageList)) {
                result.setId(tagManageList.get(0).getId());
                tags.add(result);
                continue;
            }
            TagManageEntity tagManageEntity = new TagManageEntity();
            BeanUtils.copyProperties(tagManageParam, tagManageEntity);
            tagManageMapper.insert(tagManageEntity);
            result.setId(tagManageEntity.getId());

            tags.add(result);
        }
        return tags;
    }

    /**
     * 根据name和类型批量查询数据源标签
     *
     * @param tagNames
     * @param type
     * @return
     */
    @Override
    public List<TagManageResult> selectDataSourceTagsByNamesAndType(List<String> tagNames, Integer type) {
        LambdaQueryWrapper<TagManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TagManageEntity::getTagName, tagNames)
                .eq(TagManageEntity::getTagStatus, 0)
                .eq(TagManageEntity::getTagType,type);
        List<TagManageEntity> tagManageEntityList = tagManageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(tagManageEntityList)) {
            return Collections.emptyList();
        }
        return tagManageEntityList.stream().map(tagManageEntity -> {
            TagManageResult tagManageResult = new TagManageResult();
            tagManageResult.setId(tagManageEntity.getId());
            tagManageResult.setTagName(tagManageEntity.getTagName());
            return tagManageResult;
        }).collect(Collectors.toList());
    }
}
