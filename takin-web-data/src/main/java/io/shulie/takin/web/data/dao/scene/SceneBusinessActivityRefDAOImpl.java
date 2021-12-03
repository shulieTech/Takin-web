package io.shulie.takin.web.data.dao.scene;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.mapper.mysql.SceneBusinessActivityRefMapper;
import io.shulie.takin.web.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: 南风
 * @Date: 2021/11/16 10:22 上午
 */
@Service
public class SceneBusinessActivityRefDAOImpl extends ServiceImpl<SceneBusinessActivityRefMapper, SceneBusinessActivityRefEntity>
        implements  MPUtil<SceneBusinessActivityRefEntity>,SceneBusinessActivityRefDAO{


    private LambdaQueryWrapper<SceneBusinessActivityRefEntity> buildQuery(LambdaQueryWrapper<SceneBusinessActivityRefEntity> LambdaQueryWrapper) {
        if (Objects.isNull(LambdaQueryWrapper)) {
            return this.getLambdaQueryWrapper().eq(SceneBusinessActivityRefEntity::getIsDeleted, 0);
        } else {
            return LambdaQueryWrapper.eq(SceneBusinessActivityRefEntity::getIsDeleted, 0);
        }
    }



    @Override
    public List<Long> getAppIdList(Long businessActivityId) {
        LambdaQueryWrapper<SceneBusinessActivityRefEntity> queryWrapper = this.buildQuery(this.getQueryWrapper().lambda());
        queryWrapper.eq(SceneBusinessActivityRefEntity::getBusinessActivityId,businessActivityId);
        SceneBusinessActivityRefEntity entity = this.getOne(queryWrapper);
        if(Objects.isNull(entity)){
            return Collections.emptyList();
        }
        String applicationIds = entity.getApplicationIds();
        return Arrays.stream(StringUtils.split(applicationIds, ","))
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toList());
    }
}
