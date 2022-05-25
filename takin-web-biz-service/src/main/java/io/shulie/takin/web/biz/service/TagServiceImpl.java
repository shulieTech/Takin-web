package io.shulie.takin.web.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.web.data.mapper.mysql.TagManageMapper;
import io.shulie.takin.web.data.model.mysql.TagManageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagManageMapper tagManageMapper;

    @Override
    public Long getIdByName(String project_id) {
        LambdaQueryWrapper<TagManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                TagManageEntity::getId,
                TagManageEntity::getTagName
        );
        wrapper.eq(TagManageEntity::getTagName, project_id);
        TagManageEntity entity = tagManageMapper.selectOne(wrapper);
        if (null != entity) {
            return entity.getId();
        }
        return null;
    }
}
