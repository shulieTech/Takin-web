package io.shulie.takin.web.data.dao.scriptmanage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.ScriptFileRefMapper;
import io.shulie.takin.web.data.model.mysql.ScriptFileRefEntity;
import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaoyong
 */
@Component
public class ScriptFileRefDAOImpl implements ScriptFileRefDAO{

    @Autowired
    private ScriptFileRefMapper scriptFileRefMapper;

    @Override
    public List<ScriptFileRefResult> selectFileIdsByScriptDeployId(Long scriptDeployId) {
        LambdaQueryWrapper<ScriptFileRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                ScriptFileRefEntity::getId,
                ScriptFileRefEntity::getScriptDeployId,
                ScriptFileRefEntity::getFileId);
        wrapper.eq(ScriptFileRefEntity::getScriptDeployId,scriptDeployId);
        List<ScriptFileRefEntity> scriptFileRefEntities = scriptFileRefMapper.selectList(wrapper);
        return getScriptFileRefResults(scriptFileRefEntities);
    }


    @Override
    public void deleteByIds(List<Long> scriptFileRefIds) {
        if (CollectionUtils.isNotEmpty(scriptFileRefIds)){
            scriptFileRefMapper.deleteBatchIds(scriptFileRefIds);
        }
    }

    @Override
    public List<ScriptFileRefResult> selectFileIdsByScriptDeployIds(List<Long> scriptDeployIds) {
        if (CollectionUtils.isEmpty(scriptDeployIds)){
            return null;
        }
        LambdaQueryWrapper<ScriptFileRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                ScriptFileRefEntity::getId,
                ScriptFileRefEntity::getScriptDeployId,
                ScriptFileRefEntity::getFileId);
        wrapper.in(ScriptFileRefEntity::getScriptDeployId,scriptDeployIds);
        List<ScriptFileRefEntity> scriptFileRefEntities = scriptFileRefMapper.selectList(wrapper);
        return getScriptFileRefResults(scriptFileRefEntities);
    }

    @Override
    public void createScriptFileRefs(List<Long> fileIds, Long scriptDeployId) {
        if (CollectionUtils.isNotEmpty(fileIds) && scriptDeployId != null){
            fileIds.forEach(fileId ->{
                ScriptFileRefEntity scriptFileRefEntity = new ScriptFileRefEntity();
                scriptFileRefEntity.setFileId(fileId);
                scriptFileRefEntity.setScriptDeployId(scriptDeployId);
                scriptFileRefMapper.insert(scriptFileRefEntity);
            });
        }
    }

    @Override
    public ScriptFileRefResult selectByFileManageId(Long fileManageId) {
        LambdaQueryWrapper<ScriptFileRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                ScriptFileRefEntity::getId,
                ScriptFileRefEntity::getScriptDeployId,
                ScriptFileRefEntity::getFileId);
        wrapper.eq(ScriptFileRefEntity::getFileId,fileManageId);
        ScriptFileRefEntity scriptFileRefEntity = scriptFileRefMapper.selectOne(wrapper);
        ScriptFileRefResult scriptFileRefResult = new ScriptFileRefResult();
        scriptFileRefResult.setId(scriptFileRefEntity.getId());
        scriptFileRefResult.setFileId(scriptFileRefEntity.getFileId());
        scriptFileRefResult.setScriptDeployId(scriptFileRefEntity.getScriptDeployId());
        return scriptFileRefResult;
    }

    @Override
    public List<ScriptFileRefResult> selectByFileManageIds(List<Long> fileManageIds) {
        if (CollectionUtils.isEmpty(fileManageIds)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<ScriptFileRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                ScriptFileRefEntity::getId,
                ScriptFileRefEntity::getScriptDeployId,
                ScriptFileRefEntity::getFileId);
        wrapper.in(ScriptFileRefEntity::getFileId,fileManageIds);
        List<ScriptFileRefEntity> scriptFileRefEntities = scriptFileRefMapper.selectList(wrapper);
        return scriptFileRefEntities.stream().map(scriptFileRefEntity -> {
            ScriptFileRefResult scriptFileRefResult = new ScriptFileRefResult();
            scriptFileRefResult.setId(scriptFileRefEntity.getId());
            scriptFileRefResult.setFileId(scriptFileRefEntity.getFileId());
            scriptFileRefResult.setScriptDeployId(scriptFileRefEntity.getScriptDeployId());
            return scriptFileRefResult;
        }).collect(Collectors.toList());
    }

    @Override
    public void batchUpdateIds(List<ScriptFileRefEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
           return;
        }
        for(ScriptFileRefEntity scriptFileRefEntity : entities) {
            scriptFileRefMapper.updateById(scriptFileRefEntity);
        }

    }

    private List<ScriptFileRefResult> getScriptFileRefResults(List<ScriptFileRefEntity> scriptFileRefEntities){
        if (CollectionUtils.isNotEmpty(scriptFileRefEntities)){
            return scriptFileRefEntities.stream().map(scriptFileRefEntity -> {
                ScriptFileRefResult scriptFileRefResult = new ScriptFileRefResult();
                scriptFileRefResult.setId(scriptFileRefEntity.getId());
                scriptFileRefResult.setFileId(scriptFileRefEntity.getFileId());
                scriptFileRefResult.setScriptDeployId(scriptFileRefEntity.getScriptDeployId());
                return scriptFileRefResult;
            }).collect(Collectors.toList());
        }
        return null;
    }
}
