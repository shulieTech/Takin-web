package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.ShadowMqConsumerDAO;
import io.shulie.takin.web.data.mapper.mysql.ShadowMqConsumerMapper;
import io.shulie.takin.web.data.model.mysql.ShadowMqConsumerEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/4/8 2:21 下午
 */
@Service
public class ShadowMqConsumerDAOImpl
        extends ServiceImpl<ShadowMqConsumerMapper, ShadowMqConsumerEntity>
        implements ShadowMqConsumerDAO, MPUtil<ShadowMqConsumerEntity> {

    @Override
    public List<ShadowMqConsumerEntity> listByApplicationId(Long applicationId) {
        return this.list(this.getLambdaQueryWrapper().eq(ShadowMqConsumerEntity::getApplicationId, applicationId));
    }

    @Override
    public void updateAppName(Long applicationId,String appName) {
        LambdaUpdateWrapper<ShadowMqConsumerEntity> wrapper = this.getLambdaUpdateWrapper();
        wrapper.eq(ShadowMqConsumerEntity::getApplicationId,applicationId);
        wrapper.set(ShadowMqConsumerEntity::getApplicationName,appName);
        this.update(wrapper);
    }

}
