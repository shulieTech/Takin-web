package io.shulie.takin.web.data.dao.scene;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.NumberUtil;
import com.pamirs.takin.entity.dao.linkmanage.TBusinessLinkManageTableMapper;
import io.shulie.takin.web.data.convert.linkmanage.BusinessLinkManageConvert;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.mapper.mysql.SceneLinkRelateMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateQuery;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 无涯
 * @date 2021/5/28 5:38 下午
 */
@Service
public class SceneLinkRelateDAOImpl extends ServiceImpl<SceneLinkRelateMapper, SceneLinkRelateEntity>
        implements SceneLinkRelateDAO, MPUtil<SceneLinkRelateEntity> {

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    @Override
    public List<SceneLinkRelateResult> getList(SceneLinkRelateParam param) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper = this.getLambdaQueryWrapper();
        if (CollectionUtils.isNotEmpty(param.getSceneIds())) {
            wrapper.in(SceneLinkRelateEntity::getSceneId, param.getSceneIds());
        }
        wrapper.eq(SceneLinkRelateEntity::getIsDeleted, 0);
        List<SceneLinkRelateEntity> entities = this.list(wrapper);
        return toResult(entities);
    }

    /**
     * 查询接口
     */
    @Override
    public List<SceneLinkRelateResult> query(SceneLinkRelateQuery query) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper = this.getLambdaQueryWrapper();
        if (null != query.getTenantId()) {
            wrapper.eq(SceneLinkRelateEntity::getTenantId, query.getTenantId());
        }
        if (StringUtils.isNotBlank(query.getEnvCode())) {
            wrapper.eq(SceneLinkRelateEntity::getEnvCode, query.getEnvCode());
        }
        if (StringUtils.isNotBlank(query.getXpathMd5())) {
            wrapper.eq(SceneLinkRelateEntity::getScriptXpathMd5, query.getXpathMd5());
        }
        if (StringUtils.isNotBlank(query.getEntrance())) {
            wrapper.eq(SceneLinkRelateEntity::getEntrance, query.getEntrance());
        }
        if (null != query.getSceneId()) {
            wrapper.eq(SceneLinkRelateEntity::getSceneId, query.getSceneId());
        }
        wrapper.orderByDesc(SceneLinkRelateEntity::getId);
        List<SceneLinkRelateEntity> entities = this.list(wrapper);
        return toResult(entities);
    }

    @Override
    public List<SceneLinkRelateResult> getByEntrance(String entrance) {
        if (StringUtils.isBlank(entrance)) {
            return null;
        }
        SceneLinkRelateQuery query = new SceneLinkRelateQuery();
        query.setEntrance(entrance);
        return query(query);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> oldIds) {
        if (CollectionUtils.isEmpty(oldIds)) {
            return;
        }
        List<SceneLinkRelateEntity> sceneLinkRelateEntities = this.listByIds(oldIds);
        if (CollectionUtils.isEmpty(sceneLinkRelateEntities)) {
            return;
        }
        if (CollectionUtils.isNotEmpty(oldIds)) {
            this.removeByIds(oldIds);
        }
        //删除关联关系之后，将业务活动置为可以被删除
        List<String> businessLinkIds = sceneLinkRelateEntities.stream().map(SceneLinkRelateEntity::getBusinessLinkId)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(businessLinkIds)) {
            return;
        }
        //如果存在改业务活动还有其他的业务流程关联着，这个业务活动也不能被设置为可删除
        LambdaQueryWrapper<SceneLinkRelateEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.in(SceneLinkRelateEntity::getBusinessLinkId, businessLinkIds);
        List<SceneLinkRelateEntity> linkRelateEntities = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(linkRelateEntities)) {
            List<String> stringList = linkRelateEntities.stream().map(SceneLinkRelateEntity::getBusinessLinkId).
                    collect(Collectors.toList());
            businessLinkIds = businessLinkIds.stream().filter(o -> !stringList.contains(o)).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(businessLinkIds)) {
            return;
        }
        List<Long> businessLinkIdList = businessLinkIds.stream().map(NumberUtils::toLong).collect(Collectors.toList());
        BusinessLinkManageTableEntity businessLinkManageTableEntity = new BusinessLinkManageTableEntity();
        businessLinkManageTableEntity.setCanDelete(0);
        LambdaQueryWrapper<BusinessLinkManageTableEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BusinessLinkManageTableEntity::getLinkId, businessLinkIdList);
        businessLinkManageTableMapper.update(businessLinkManageTableEntity, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<SceneLinkRelateSaveParam> saveParams) {
        if (CollectionUtils.isEmpty(saveParams)) {
            return;
        }
        //新增关联关系之后，将业务活动置为不能被删除
        List<Long> businessLinkId = saveParams.stream().map(o -> NumberUtils.toLong(o.getBusinessLinkId())).
                collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(businessLinkId)) {
            BusinessLinkManageTableEntity businessLinkManageTableEntity = new BusinessLinkManageTableEntity();
            businessLinkManageTableEntity.setCanDelete(1);
            LambdaQueryWrapper<BusinessLinkManageTableEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(BusinessLinkManageTableEntity::getLinkId, businessLinkId);
            businessLinkManageTableMapper.update(businessLinkManageTableEntity, wrapper);
        }
        List<SceneLinkRelateEntity> sceneLinkRelateEntities = BusinessLinkManageConvert.INSTANCE.ofSceneLinkRelateSaveParams(saveParams);
        this.saveBatch(sceneLinkRelateEntities);
    }

    private List<SceneLinkRelateResult> toResult(List<SceneLinkRelateEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.stream().filter(Objects::nonNull)
                .map(this::toResult)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private SceneLinkRelateResult toResult(SceneLinkRelateEntity entity) {
        if (null == entity) {
            return null;
        }
        SceneLinkRelateResult result = new SceneLinkRelateResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }
}
