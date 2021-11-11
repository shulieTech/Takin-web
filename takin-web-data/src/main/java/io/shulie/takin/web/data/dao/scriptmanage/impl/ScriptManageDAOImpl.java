package io.shulie.takin.web.data.dao.scriptmanage.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import io.shulie.takin.common.beans.page.PagingList;

import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.mapper.mysql.ScriptExecuteResultMapper;
import io.shulie.takin.web.common.enums.script.ScriptManageDeployStatusEnum;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptManageDAO;
import io.shulie.takin.web.data.mapper.mysql.ScriptManageDeployMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptManageMapper;
import io.shulie.takin.web.data.model.mysql.ScriptExecuteResultEntity;
import io.shulie.takin.web.data.model.mysql.ScriptManageDeployEntity;
import io.shulie.takin.web.data.model.mysql.ScriptManageEntity;
import io.shulie.takin.web.data.param.scriptmanage.ScriptExecuteResultCreateParam;
import io.shulie.takin.web.data.param.scriptmanage.ScriptManageDeployCreateParam;
import io.shulie.takin.web.data.param.scriptmanage.ScriptManageDeployPageQueryParam;
import io.shulie.takin.web.data.param.scriptmanage.shell.ShellExecuteParam;
import io.shulie.takin.web.data.result.scriptmanage.ScriptExecuteResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageDeployResult;
import io.shulie.takin.web.data.result.scriptmanage.ScriptManageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Slf4j
@Component
public class ScriptManageDAOImpl
    extends ServiceImpl<ScriptManageMapper, ScriptManageEntity>
    implements ScriptManageDAO {

    @Autowired
    private ScriptManageMapper scriptManageMapper;
    @Autowired
    private ScriptManageDeployMapper scriptManageDeployMapper;
    @Autowired
    private ScriptExecuteResultMapper scriptExecuteResultMapper;

    @Override
    public ScriptManageResult selectScriptManageById(Long scriptId) {
        ScriptManageResult scriptManageResult = new ScriptManageResult();
        ScriptManageEntity scriptManageEntity = scriptManageMapper.selectById(scriptId);
        BeanUtils.copyProperties(scriptManageEntity, scriptManageResult);
        return scriptManageResult;
    }

    @Override
    public ScriptManageDeployResult selectScriptManageDeployById(Long scriptDeployId) {
        ScriptManageDeployEntity scriptManageDeployEntity = scriptManageDeployMapper.selectById(scriptDeployId);
        if (scriptManageDeployEntity == null) {
            return null;
        }

        return BeanUtil.copyProperties(scriptManageDeployEntity, ScriptManageDeployResult.class);
    }

    @Override
    public ScriptManageDeployResult selectScriptManageDeployByVersion(Long scriptId, Integer scriptVersion) {
        LambdaQueryWrapper<ScriptManageDeployEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptManageDeployEntity::getScriptId, scriptId);
        wrapper.eq(ScriptManageDeployEntity::getScriptVersion, scriptVersion);
        List<ScriptManageDeployEntity> entities = scriptManageDeployMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            ScriptManageDeployEntity entity = entities.get(0);
            ScriptManageDeployResult result = new ScriptManageDeployResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }
        return null;
    }

    @Override
    public void deleteScriptManageDeployById(Long scriptDeployId) {
        ScriptManageDeployEntity scriptManageDeployEntity = scriptManageDeployMapper.selectById(scriptDeployId);
        if (scriptManageDeployEntity == null) {
            return;
        }
        scriptManageDeployMapper.deleteById(scriptDeployId);
        LambdaQueryWrapper<ScriptManageDeployEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptManageDeployEntity::getScriptId, scriptManageDeployEntity.getScriptId());
        Long count = scriptManageDeployMapper.selectCount(wrapper);
        if (count == 0) {
            scriptManageMapper.deleteById(scriptManageDeployEntity.getScriptId());
        }
    }

    @Override
    public PagingList<ScriptManageDeployResult> pageQueryScriptManageDeploy(ScriptManageDeployPageQueryParam param) {
        LambdaQueryWrapper<ScriptManageDeployEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            ScriptManageDeployEntity::getId,
            ScriptManageDeployEntity::getGmtUpdate,
            ScriptManageDeployEntity::getName,
            ScriptManageDeployEntity::getRefType,
            ScriptManageDeployEntity::getRefValue,
            ScriptManageDeployEntity::getScriptId,
            ScriptManageDeployEntity::getScriptVersion,
            ScriptManageDeployEntity::getStatus,
            ScriptManageDeployEntity::getType);
        if (StringUtils.isNotBlank(param.getName())) {
            wrapper.like(ScriptManageDeployEntity::getName, param.getName());
        }
        if (StringUtils.isNotBlank(param.getRefType())) {
            wrapper.eq(ScriptManageDeployEntity::getRefType, param.getRefType());
        }
        if (StringUtils.isNotBlank(param.getRefValue())) {
            wrapper.eq(ScriptManageDeployEntity::getRefValue, param.getRefValue());
        }
        if (CollectionUtils.isNotEmpty(param.getScriptDeployIds())) {
            wrapper.in(ScriptManageDeployEntity::getId, param.getScriptDeployIds());
        }
        if (CollectionUtils.isNotEmpty(param.getScriptIds())) {
            wrapper.in(ScriptManageDeployEntity::getScriptId, param.getScriptIds());
        }
        //这个分页工具是从1开始分页的
        Page<ScriptManageDeployEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        wrapper.orderByDesc(ScriptManageDeployEntity::getGmtUpdate);
        Page<ScriptManageDeployEntity> scriptManageDeployEntityPage = scriptManageDeployMapper.selectPage(page,
            wrapper);
        if (CollectionUtils.isEmpty(scriptManageDeployEntityPage.getRecords())) {
            return PagingList.of(Lists.newArrayList(),scriptManageDeployEntityPage.getTotal());
        }
        List<ScriptManageDeployResult> scriptManageDeployResults = scriptManageDeployEntityPage.getRecords().stream()
            .map(scriptManageDeployEntity -> {
                ScriptManageDeployResult scriptManageDeployResult = new ScriptManageDeployResult();
                BeanUtils.copyProperties(scriptManageDeployEntity, scriptManageDeployResult);
                return scriptManageDeployResult;
            }).collect(Collectors.toList());

        return PagingList.of(scriptManageDeployResults, scriptManageDeployEntityPage.getTotal());
    }

    @Override
    public ScriptManageDeployResult createScriptManageDeploy(ScriptManageDeployCreateParam scriptManageDeployCreateParam) {
        if (scriptManageDeployCreateParam == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "创建部署脚本时入参为空！");
        }

        ScriptManageDeployEntity scriptManageDeployEntity = new ScriptManageDeployEntity();
        try {
            if (scriptManageDeployCreateParam.getScriptId() == null) {
                // 需要新增脚本
                ScriptManageEntity scriptManageEntity = new ScriptManageEntity();
                scriptManageEntity.setName(scriptManageDeployCreateParam.getName());
                scriptManageEntity.setScriptVersion(scriptManageDeployCreateParam.getScriptVersion());
                scriptManageEntity.setFeature(scriptManageDeployCreateParam.getFeature());
                scriptManageMapper.insert(scriptManageEntity);
                scriptManageDeployCreateParam.setScriptId(scriptManageEntity.getId());
            }

            BeanUtils.copyProperties(scriptManageDeployCreateParam, scriptManageDeployEntity);
            scriptManageDeployMapper.insert(scriptManageDeployEntity);
        } catch (Throwable e) {
            log.error("创建部署脚本出现未知异常", e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_ADD_ERROR, "创建部署脚本失败！请查看takin-web日志。");
        }

        ScriptManageDeployResult scriptManageDeployResult = new ScriptManageDeployResult();
        BeanUtils.copyProperties(scriptManageDeployEntity, scriptManageDeployResult);
        return scriptManageDeployResult;
    }

    @Override
    public List<ScriptManageResult> selectScriptManageByName(String name) {
        LambdaQueryWrapper<ScriptManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            ScriptManageEntity::getId,
            ScriptManageEntity::getName,
            ScriptManageEntity::getScriptVersion
        );
        wrapper.eq(ScriptManageEntity::getName, name);
        List<ScriptManageEntity> scriptManageEntities = scriptManageMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(scriptManageEntities)) {
            return scriptManageEntities.stream().map(scriptManageEntity -> {
                ScriptManageResult scriptManageResult = new ScriptManageResult();
                scriptManageResult.setId(scriptManageEntity.getId());
                scriptManageResult.setName(scriptManageEntity.getName());
                scriptManageResult.setScriptVersion(scriptManageEntity.getScriptVersion());
                return scriptManageResult;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void updateScriptVersion(Long scriptId, Integer scriptVersion) {
        ScriptManageEntity scriptManageEntity = new ScriptManageEntity();
        scriptManageEntity.setId(scriptId);
        scriptManageEntity.setScriptVersion(scriptVersion);
        this.updateById(scriptManageEntity);

        // 将关联的脚本实例更新为历史状态
        LambdaUpdateWrapper<ScriptManageDeployEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ScriptManageDeployEntity::getScriptId, scriptId);
        // TODO 这里记一下 脚本实例没有更新版本号
        ScriptManageDeployEntity scriptManageDeployEntity = new ScriptManageDeployEntity();
        scriptManageDeployEntity.setStatus(ScriptManageDeployStatusEnum.HISTORY.getCode());
        scriptManageDeployMapper.update(scriptManageDeployEntity, wrapper);
    }

    @Override
    public void switchScriptVersion(Long scriptId, Integer scriptVersion) {
        ScriptManageEntity scriptManageEntity = new ScriptManageEntity();
        scriptManageEntity.setId(scriptId);
        scriptManageEntity.setScriptVersion(scriptVersion);
        scriptManageMapper.updateById(scriptManageEntity);
        //将关联的脚本更新为历史状态
        LambdaUpdateWrapper<ScriptManageDeployEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ScriptManageDeployEntity::getScriptId, scriptId);
        ScriptManageDeployEntity scriptManageDeployEntity = new ScriptManageDeployEntity();
        scriptManageDeployEntity.setStatus(2);
        scriptManageDeployMapper.update(scriptManageDeployEntity, wrapper);
        // 当前版本切换正常版本
        LambdaUpdateWrapper<ScriptManageDeployEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScriptManageDeployEntity::getScriptId, scriptId);
        updateWrapper.eq(ScriptManageDeployEntity::getScriptVersion, scriptVersion);
        ScriptManageDeployEntity updateEntity = new ScriptManageDeployEntity();
        updateEntity.setStatus(1);
        scriptManageDeployMapper.update(updateEntity, updateWrapper);
    }

    @Override
    public List<ScriptManageDeployResult> selectByRefIdsAndType(List<String> refValues, String refType,
        List<Integer> statusList) {
        LambdaQueryWrapper<ScriptManageDeployEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            ScriptManageDeployEntity::getId,
            ScriptManageDeployEntity::getGmtUpdate,
            ScriptManageDeployEntity::getName,
            ScriptManageDeployEntity::getRefType,
            ScriptManageDeployEntity::getRefValue,
            ScriptManageDeployEntity::getScriptId,
            ScriptManageDeployEntity::getScriptVersion);
        wrapper.orderByDesc(ScriptManageDeployEntity::getGmtUpdate);
        wrapper.in(ScriptManageDeployEntity::getRefValue, refValues);
        wrapper.eq(ScriptManageDeployEntity::getRefType, refType);
        if (CollectionUtils.isNotEmpty(statusList)) {
            wrapper.in(ScriptManageDeployEntity::getStatus, statusList);
        }
        List<ScriptManageDeployEntity> scriptManageDeployEntities = scriptManageDeployMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(scriptManageDeployEntities)) {
            return scriptManageDeployEntities.stream().map(scriptManageDeployEntity -> {
                ScriptManageDeployResult scriptManageDeployResult = new ScriptManageDeployResult();
                BeanUtils.copyProperties(scriptManageDeployEntity, scriptManageDeployResult);
                return scriptManageDeployResult;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public PagingList<ScriptManageDeployResult> pageQueryRecentScriptManageDeploy(
        ScriptManageDeployPageQueryParam param) {
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        statusList.add(0);
        param.setStatusList(statusList);
        List<ScriptManageDeployResult> scriptManageDeployResults = queryScriptManageDeploy(param);
        if (CollectionUtils.isEmpty(scriptManageDeployResults)) {
            return PagingList.empty();
        }
        List<Long> scriptIdList = scriptManageDeployResults.stream().map(ScriptManageDeployResult::getScriptId).collect(
            Collectors.toList());
        LambdaQueryWrapper<ScriptManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            ScriptManageEntity::getId,
            ScriptManageEntity::getName,
            ScriptManageEntity::getGmtUpdate,
            ScriptManageEntity::getScriptVersion,
            ScriptManageEntity::getTenantId,
            ScriptManageEntity::getUserId
        );
        wrapper.in(ScriptManageEntity::getId, scriptIdList);
        Page<ScriptManageEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        wrapper.orderByDesc(ScriptManageEntity::getGmtUpdate);
        if (CollectionUtils.isNotEmpty(param.getUserIdList())) {
            wrapper.in(ScriptManageEntity::getUserId, param.getUserIdList());
        }
        Page<ScriptManageEntity> scriptManageEntityPage = scriptManageMapper.selectPage(page, wrapper);
        if (scriptManageEntityPage == null) {
            return PagingList.empty();
        }
        if (scriptManageEntityPage.getRecords().isEmpty()) {
            return PagingList.of(Lists.newArrayList(),scriptManageEntityPage.getTotal());
        }
        Map<Long, List<ScriptManageDeployResult>> longListMap = scriptManageDeployResults.stream().collect(
            Collectors.groupingBy(ScriptManageDeployResult::getScriptId));
        List<ScriptManageDeployResult> scriptManageDeploys = scriptManageEntityPage.getRecords().stream().map(
            scriptManageEntity -> {
                List<ScriptManageDeployResult> scriptManageDeployList = longListMap.get(scriptManageEntity.getId());
                if (CollectionUtils.isEmpty(scriptManageDeployList)) {
                    log.error("脚本没有找到对应的脚本实例，数据存在问题，scriptId:{}", scriptManageEntity.getId());
                    return null;
                }
                List<ScriptManageDeployResult> collect = scriptManageDeployList.stream().filter(
                    o -> o.getScriptId().equals(scriptManageEntity.getId()) &&
                        o.getScriptVersion().equals(scriptManageEntity.getScriptVersion())).collect(
                    Collectors.toList());
                return collect.get(0);
            }).collect(Collectors.toList());
        List<ScriptManageEntity> entityList = scriptManageEntityPage.getRecords();
        for (ScriptManageDeployResult result : scriptManageDeploys) {
            ScriptManageEntity entity = entityList
                .stream()
                .filter(scriptManageEntity -> scriptManageEntity.getId().equals(result.getScriptId()))
                .findFirst().get();
            result.setTenantId(entity.getTenantId());
            result.setUserId(entity.getUserId());
        }
        return PagingList.of(scriptManageDeploys, scriptManageEntityPage.getTotal());
    }

    @Override
    public void deleteScriptManageAndDeploy(Long scriptId) {
        scriptManageMapper.deleteById(scriptId);
        LambdaQueryWrapper<ScriptManageDeployEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptManageDeployEntity::getScriptId, scriptId);
        scriptManageDeployMapper.delete(wrapper);
    }

    @Override
    public List<ScriptManageDeployResult> selectScriptManageDeployByScriptId(Long scriptId) {
        ScriptManageDeployPageQueryParam pageQueryParam = new ScriptManageDeployPageQueryParam();
        pageQueryParam.setScriptIds(Collections.singletonList(scriptId));
        return queryScriptManageDeploy(pageQueryParam);
    }

    private List<ScriptManageDeployResult> queryScriptManageDeploy(ScriptManageDeployPageQueryParam param) {
        LambdaQueryWrapper<ScriptManageDeployEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            ScriptManageDeployEntity::getId,
            ScriptManageDeployEntity::getGmtUpdate,
            ScriptManageDeployEntity::getName,
            ScriptManageDeployEntity::getRefType,
            ScriptManageDeployEntity::getRefValue,
            ScriptManageDeployEntity::getScriptId,
            ScriptManageDeployEntity::getScriptVersion,
            ScriptManageDeployEntity::getStatus,
            ScriptManageDeployEntity::getType);
        if (StringUtils.isNotBlank(param.getName())) {
            wrapper.like(ScriptManageDeployEntity::getName, param.getName());
        }
        if (StringUtils.isNotBlank(param.getRefType())) {
            wrapper.eq(ScriptManageDeployEntity::getRefType, param.getRefType());
        }
        if (StringUtils.isNotBlank(param.getRefValue())) {
            wrapper.eq(ScriptManageDeployEntity::getRefValue, param.getRefValue());
        }
        if (CollectionUtils.isNotEmpty(param.getStatusList())) {
            wrapper.in(ScriptManageDeployEntity::getStatus, param.getStatusList());
        }
        if (CollectionUtils.isNotEmpty(param.getScriptDeployIds())) {
            wrapper.in(ScriptManageDeployEntity::getId, param.getScriptDeployIds());
        }
        if (CollectionUtils.isNotEmpty(param.getScriptIds())) {
            wrapper.in(ScriptManageDeployEntity::getScriptId, param.getScriptIds());
        }
        if (param.getScriptType() != null) {
            wrapper.eq(ScriptManageDeployEntity::getType, param.getScriptType());
        }
        wrapper.orderByDesc(ScriptManageDeployEntity::getGmtUpdate);
        List<ScriptManageDeployEntity> scriptManageDeployEntities = scriptManageDeployMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(scriptManageDeployEntities)) {
            return new ArrayList<>();
        }
        return scriptManageDeployEntities.stream().map(scriptManageDeployEntity -> {
            ScriptManageDeployResult scriptManageDeployResult = new ScriptManageDeployResult();
            BeanUtils.copyProperties(scriptManageDeployEntity, scriptManageDeployResult);
            return scriptManageDeployResult;
        }).collect(Collectors.toList());

    }

    /**
     * 指定责任人-脚本列表
     *
     * @param scriptId 脚本id
     * @param userId   负责人id
     * @return
     */
    @Override
    public int allocationUser(Long scriptId, Long userId) {
        ScriptManageDeployEntity deployEntity = scriptManageDeployMapper.selectById(scriptId);
        if (null == deployEntity) {
            return 0;
        }
        scriptId = deployEntity.getScriptId();
        LambdaUpdateWrapper<ScriptManageEntity> wrapper = new LambdaUpdateWrapper();
        wrapper.set(ScriptManageEntity::getUserId, userId)
            .eq(ScriptManageEntity::getId, scriptId);
        return scriptManageMapper.update(null, wrapper);
    }

    @Override
    public ScriptManageDeployEntity getDeployByDeployId(Long scriptDeployId) {
        return scriptManageDeployMapper.selectById(scriptDeployId);
    }

    @Override
    public Map<Long, Long> selectScriptDeployNumResult() {
        LambdaQueryWrapper<ScriptManageDeployEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            ScriptManageDeployEntity::getScriptId,
            ScriptManageDeployEntity::getId
        );
        List<ScriptManageDeployEntity> list = scriptManageDeployMapper.selectList(wrapper);
        Map<Long, Long> map = list.stream().collect(Collectors.groupingBy(ScriptManageDeployEntity::getScriptId, Collectors.counting()));
        return map;
    }

    @Override
    public Long createScriptExecuteResult(ScriptExecuteResultCreateParam param) {
        ScriptExecuteResultEntity entity = new ScriptExecuteResultEntity();
        BeanUtils.copyProperties(param, entity);
        scriptExecuteResultMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public PagingList<ScriptExecuteResult> getExecuteResult(ShellExecuteParam param) {
        LambdaQueryWrapper<ScriptExecuteResultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptExecuteResultEntity::getScriptId, param.getScriptId());
        wrapper.orderByDesc(ScriptExecuteResultEntity::getGmtCreate);
        Page<ScriptExecuteResultEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        Page<ScriptExecuteResultEntity> pageList = scriptExecuteResultMapper.selectPage(page, wrapper);
        if (pageList == null) {
            return PagingList.empty();
        }
        if( pageList.getRecords().isEmpty()) {
            return PagingList.of(Lists.newArrayList(),pageList.getTotal());
        }
        List<ScriptExecuteResultEntity> list = pageList.getRecords();
        List<ScriptExecuteResult> results = list.stream().map(entity -> {
            ScriptExecuteResult result = new ScriptExecuteResult();
            BeanUtils.copyProperties(entity, result);
            result.setGmtCreate(DateUtils.dateToString(entity.getGmtCreate(), DateUtils.FORMATE_YMDHMS));
            return result;
        }).collect(Collectors.toList());
        return PagingList.of(results, pageList.getTotal());
    }
}
