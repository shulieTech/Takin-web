package io.shulie.takin.web.biz.engine.extractor;

import java.util.Objects;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigSceneRelateShipMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import org.springframework.stereotype.Component;

@Component
public class InterfacePerformanceSelectedExtractor implements SelectedExtractor {

    @Resource
    private InterfacePerformanceConfigSceneRelateShipMapper interfacePerformanceConfigSceneRelateShipMapper;

    @Override
    public WatchmanClusterResponse extract(ExtractorContext context) {
        LambdaQueryWrapper<InterfacePerformanceConfigSceneRelateShipEntity> wrapper =
            Wrappers.lambdaQuery(InterfacePerformanceConfigSceneRelateShipEntity.class)
                .eq(InterfacePerformanceConfigSceneRelateShipEntity::getApiId, context.getId())
                .eq(InterfacePerformanceConfigSceneRelateShipEntity::getIsDeleted, 0);
        InterfacePerformanceConfigSceneRelateShipEntity entity = interfacePerformanceConfigSceneRelateShipMapper.selectOne(wrapper);
        if (Objects.isNull(entity)) {
            return DEFAULT_RESPONSE;
        }
        return extraFromFeatures(entity.getFeatures());
    }

    @Override
    public ExtractorType type() {
        return ExtractorType.INTERFACE_PERFORMANCE;
    }
}
