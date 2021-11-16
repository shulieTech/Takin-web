package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginTenantRefDAO;
import io.shulie.takin.web.data.mapper.mysql.PluginTenantRefMapper;
import io.shulie.takin.web.data.model.mysql.PluginTenantRefEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.CreatePluginTenantRefParam;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 插件版本库(PluginTenantRef)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-10 17:54:08
 */
@Service
public class PluginTenantRefDAOImpl extends ServiceImpl<PluginTenantRefMapper, PluginTenantRefEntity>
    implements PluginTenantRefDAO, MPUtil<PluginTenantRefEntity> {

    @Override
    public void batchInsert(List<CreatePluginTenantRefParam> list) {
        List<PluginTenantRefEntity> collect = list.stream()
            .map(item -> Convert.convert(PluginTenantRefEntity.class, item))
            .collect(Collectors.toList());
        this.saveBatch(collect);
    }
}

