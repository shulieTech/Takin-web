package io.shulie.takin.web.data.dao.scene;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.convert.linkmanage.BusinessLinkManageConvert;
import io.shulie.takin.web.data.mapper.mysql.SceneLinkRelateMapper;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateQuery;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    public List<SceneLinkRelateResult> getList(SceneLinkRelateParam param) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper =this.getLambdaQueryWrapper();
        if(CollectionUtils.isNotEmpty(param.getSceneIds())) {
            wrapper.in(SceneLinkRelateEntity::getSceneId,param.getSceneIds());
        }
        wrapper.eq(SceneLinkRelateEntity::getIsDeleted,0);
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
    public void batchInsertOrUpdate(List<SceneLinkRelateSaveParam> saveParams) {
        List<SceneLinkRelateEntity> sceneLinkRelateEntities = BusinessLinkManageConvert.INSTANCE.ofSceneLinkRelateSaveParams(saveParams);
        this.saveOrUpdateBatch(sceneLinkRelateEntities);
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
        BeanUtils.copyProperties(entity,result);
        return result;
    }
}
