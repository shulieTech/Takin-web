package io.shulie.takin.web.data.dao.scene;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.SceneLinkRelateMapper;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
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
    public List<SceneLinkRelateResult> getList(SceneLinkRelateParam param) {
        LambdaQueryWrapper<SceneLinkRelateEntity> wrapper =this.getLambdaQueryWrapper();
        if(CollectionUtils.isNotEmpty(param.getSceneIds())) {
            wrapper.in(SceneLinkRelateEntity::getSceneId,param.getSceneIds());
        }
        wrapper.eq(SceneLinkRelateEntity::getIsDeleted,0);
        List<SceneLinkRelateEntity> entities = this.list(wrapper);
        if(CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> {
            SceneLinkRelateResult result = new SceneLinkRelateResult();
            BeanUtils.copyProperties(entity,result);
            return result;
        }).collect(Collectors.toList());
    }
}
