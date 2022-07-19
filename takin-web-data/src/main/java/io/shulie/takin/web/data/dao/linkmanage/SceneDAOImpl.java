package io.shulie.takin.web.data.dao.linkmanage;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.excel.util.StringUtils;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.convert.linkmanage.BusinessLinkManageConvert;
import io.shulie.takin.web.data.mapper.mysql.SceneMapper;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.param.scene.ScenePageQueryParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 业务流程dao
 *
 * @author fanxx
 * @date 2020/11/4 2:57 下午
 */
@Component
public class SceneDAOImpl implements SceneDAO {

    @Resource
    private SceneMapper sceneMapper;

    @Override
    public int insert(SceneCreateParam param) {
        SceneEntity entity = new SceneEntity();
        BeanUtils.copyProperties(param, entity);
        int count = sceneMapper.insert(entity);
        param.setId(entity.getId());
        return count;
    }

    /**
     * 指定责任人-业务流程
     *
     * @param param
     * @return
     */
    @Override
    public int allocationUser(SceneUpdateParam param) {
        LambdaUpdateWrapper<SceneEntity> wrapper = new LambdaUpdateWrapper();
        wrapper.set(SceneEntity::getUserId, param.getUserId())
                .eq(SceneEntity::getId, param.getId());
        return sceneMapper.update(null, wrapper);
    }

    @Override
    public List<SceneResult> selectList(SceneQueryParam queryParam) {
        List<SceneResult> sceneResultList = Lists.newArrayList();
        LambdaQueryWrapper<SceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(queryParam.getUserIdList())) {
            queryWrapper.in(SceneEntity::getUserId, queryParam.getUserIdList());
        }
        if (!StringUtils.isEmpty(queryParam.getSceneName())) {
            queryWrapper.like(SceneEntity::getSceneName, queryParam.getSceneName());
        }
        queryWrapper.eq(SceneEntity::getIsDeleted, 0);
        queryWrapper.select(
                SceneEntity::getId,
                SceneEntity::getSceneName,
                SceneEntity::getTenantId,
                SceneEntity::getUserId);
        List<SceneEntity> sceneEntityList = sceneMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(sceneEntityList)) {
            sceneResultList = sceneEntityList.stream().map(sceneEntity -> {
                SceneResult sceneResult = new SceneResult();
                sceneResult.setId(sceneEntity.getId());
                sceneResult.setSceneName(sceneEntity.getSceneName());
                sceneResult.setTenantId(sceneEntity.getTenantId());
                sceneResult.setUserId(sceneEntity.getUserId());
                return sceneResult;
            }).collect(Collectors.toList());
        }
        return sceneResultList;
    }

    @Override
    public List<SceneResult> selectListByName(SceneQueryParam queryParam) {
        List<SceneResult> sceneResultList = Lists.newArrayList();
        LambdaQueryWrapper<SceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(queryParam.getUserIdList())) {
            queryWrapper.in(SceneEntity::getUserId, queryParam.getUserIdList());
        }
        if (!StringUtils.isEmpty(queryParam.getSceneName())) {
            queryWrapper.eq(SceneEntity::getSceneName, queryParam.getSceneName());
        }
        queryWrapper.eq(SceneEntity::getIsDeleted, 0);
        queryWrapper.select(
                SceneEntity::getId,
                SceneEntity::getSceneName,
                SceneEntity::getTenantId,
                SceneEntity::getUserId);
        List<SceneEntity> sceneEntityList = sceneMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(sceneEntityList)) {
            sceneResultList = sceneEntityList.stream().map(sceneEntity -> {
                SceneResult sceneResult = new SceneResult();
                sceneResult.setId(sceneEntity.getId());
                sceneResult.setSceneName(sceneEntity.getSceneName());
                sceneResult.setTenantId(sceneEntity.getTenantId());
                sceneResult.setUserId(sceneEntity.getUserId());
                return sceneResult;
            }).collect(Collectors.toList());
        }
        return sceneResultList;
    }

    @Override
    public int update(SceneUpdateParam sceneUpdateParam) {
        SceneEntity entity = new SceneEntity();
        BeanUtils.copyProperties(sceneUpdateParam, entity);
        LambdaQueryWrapper<SceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneEntity::getId, sceneUpdateParam.getId());
        return sceneMapper.update(entity, queryWrapper);
    }

    @Override
    public SceneResult getSceneDetail(Long id) {
        SceneEntity sceneEntity = sceneMapper.selectById(id);
        if (sceneEntity == null) {
            return null;
        }
        return BeanUtil.copyProperties(sceneEntity, SceneResult.class);
    }

    @Override
    public PagingList<SceneResult> selectPageList(ScenePageQueryParam param) {
        Page<SceneEntity> page = new Page<>();
        page.setCurrent(param.getCurrent() + 1);
        page.setSize(param.getPageSize());

        LambdaQueryWrapper<SceneEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(param.getSceneName())) {
            lambdaQueryWrapper.like(SceneEntity::getSceneName, "\\" + param.getSceneName());
        }
        if (CollectionUtils.isNotEmpty(param.getUserIdList())) {
            lambdaQueryWrapper.in(SceneEntity::getUserId, param.getUserIdList());
        }
        if (param.getIgnoreType() != null) {
            lambdaQueryWrapper.ne(SceneEntity::getType, param.getIgnoreType());
        }
        lambdaQueryWrapper.eq(SceneEntity::getIsDeleted, 0);
        lambdaQueryWrapper.orderByDesc(SceneEntity::getUpdateTime);
        Page<SceneEntity> sceneEntityPage = sceneMapper.selectPage(page, lambdaQueryWrapper);
        if (sceneEntityPage == null || CollectionUtils.isEmpty(sceneEntityPage.getRecords())) {
            return PagingList.of(Lists.newArrayList(), 0);
        }
        List<SceneResult> sceneResultList = BusinessLinkManageConvert.INSTANCE.ofSceneEntityList(
                sceneEntityPage.getRecords());
        return PagingList.of(sceneResultList, sceneEntityPage.getTotal());
    }

    @Override
    public boolean existsScene(Long tenantId, String envCode) {
        LambdaQueryWrapper<SceneEntity> wrapper = Wrappers.lambdaQuery(SceneEntity.class)
                .eq(SceneEntity::getTenantId, tenantId)
                .eq(SceneEntity::getEnvCode, envCode)
                .eq(SceneEntity::getIsDeleted, 0);
        return SqlHelper.retBool(sceneMapper.selectCount(wrapper));
    }
}
