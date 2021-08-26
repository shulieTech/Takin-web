package io.shulie.takin.web.data.dao.application.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.ShadowJobConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.ShadowJobConfigMapper;
import io.shulie.takin.web.data.model.mysql.ShadowJobConfigEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuchuan
 * @date 2021/4/8 10:51 上午
 */
@Service
public class ShadowJobConfigDAOImpl
        extends ServiceImpl<ShadowJobConfigMapper, ShadowJobConfigEntity>
        implements ShadowJobConfigDAO, MPUtil<ShadowJobConfigEntity> {

    @Override
    public List<ShadowJobConfigEntity> listByApplicationId(Long applicationId) {
        return this.list(this.getLambdaQueryWrapper().eq(ShadowJobConfigEntity::getApplicationId, applicationId));
    }

}
