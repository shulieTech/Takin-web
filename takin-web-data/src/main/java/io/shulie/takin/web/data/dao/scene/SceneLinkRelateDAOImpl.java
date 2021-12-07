package io.shulie.takin.web.data.dao.scene;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowTree;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.SceneLinkRelateMapper;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateCreateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2021/5/28 5:38 下午
 */
@Service
public class SceneLinkRelateDAOImpl extends ServiceImpl<SceneLinkRelateMapper, SceneLinkRelateEntity>
    implements SceneLinkRelateDAO, MPUtil<SceneLinkRelateEntity> {

    @Override
    public void batchInsert(List<SceneLinkRelateCreateParam> params) {
       if(CollectionUtils.isEmpty(params)) {
           return;
       }
        List<SceneLinkRelateEntity> entities = DataTransformUtil.list2list(params,SceneLinkRelateEntity.class);
       this.saveBatch(entities);
    }

    @Override
    public void deleteBySceneId(String sceneId) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper =this.getLambdaQueryWrapper();
        wrapper.eq(SceneLinkRelateEntity::getSceneId,sceneId);
        this.remove(wrapper);
    }

    @Override
    public List<SceneLinkRelateResult> selectBySceneId(Long sceneId) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper =this.getLambdaQueryWrapper();
        wrapper.eq(SceneLinkRelateEntity::getSceneId,sceneId);
        return getSceneLinkRelateResults(wrapper);
    }

    @Override
    public Long countBySceneId(Long sceneId) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper =this.getLambdaQueryWrapper();
        wrapper.eq(SceneLinkRelateEntity::getSceneId,sceneId);
        wrapper.eq(SceneLinkRelateEntity::getIsDeleted, 0);
        return this.count(wrapper);
    }

    @Override
    public int countByTechLinkIds(List<String> techLinkIds) {
       return this.getBaseMapper().countByTechLinkIds(techLinkIds);
    }

    @Override
    public long countByBusinessLinkId(Long businessLinkId) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper =this.getLambdaQueryWrapper();
        wrapper.eq(SceneLinkRelateEntity::getBusinessLinkId,businessLinkId);
        wrapper.eq(SceneLinkRelateEntity::getIsDeleted, 0);
        return this.count(wrapper);
    }

    private List<SceneLinkRelateResult> getSceneLinkRelateResults(LambdaQueryWrapper<SceneLinkRelateEntity> wrapper) {
        wrapper.eq(SceneLinkRelateEntity::getIsDeleted, 0);
        List<SceneLinkRelateEntity> entities = this.list(wrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> {
            SceneLinkRelateResult result = new SceneLinkRelateResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SceneLinkRelateResult> getList(SceneLinkRelateParam param) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper =this.getLambdaQueryWrapper();
        if(CollectionUtils.isNotEmpty(param.getSceneIds())) {
            wrapper.in(SceneLinkRelateEntity::getSceneId,param.getSceneIds());
        }
        return getSceneLinkRelateResults(wrapper);
    }

    @Override
    public List<Long> listBusinessLinkIdsByBusinessFlowId(Long businessFlowId) {
        List<Object> businessLinkObjectIds = this.listObjs(this.getLambdaQueryWrapper()
            .select(SceneLinkRelateEntity::getBusinessLinkId).eq(SceneLinkRelateEntity::getSceneId, businessFlowId));
        return DataTransformUtil.list2list(businessLinkObjectIds, Long.class);
    }

    @Override
    public List<BusinessFlowTree> listRecursion(Long flowId, Long tenantId, String envCode) {
        return this.getBaseMapper().listRecursion(flowId, tenantId, envCode);
    }

}
