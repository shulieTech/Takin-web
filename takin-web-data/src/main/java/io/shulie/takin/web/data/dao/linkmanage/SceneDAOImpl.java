package io.shulie.takin.web.data.dao.linkmanage;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.excel.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.SceneMapper;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 业务流程dao
 * @author fanxx
 * @date 2020/11/4 2:57 下午
 */
@Component
public class SceneDAOImpl implements SceneDAO {

    @Autowired
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
}
