package io.shulie.takin.web.data.dao.pradar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.mapper.mysql.PradarZkConfigMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigQueryParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author junshao
 * @date 2021/07/08 3:20 下午
 */
@Component
public class PradarZkConfigDAOImpl implements PradarZkConfigDAO {

    @Autowired
    private PradarZkConfigMapper pradarZkConfigMapper;

    @Override
    public PagingList<PradarZKConfigResult> selectPage(PradarConfigQueryParam param) {

        QueryWrapper<PradarZkConfigEntity> wrapper = new QueryWrapper();
        wrapper.select("distinct zk_path");
        if (StringUtils.isNotBlank(param.getStartTime())) {
            wrapper.ge("create_time", param.getStartTime());
        }
        if (StringUtils.isNotBlank(param.getEndTime())) {
            wrapper.le("create_time", param.getEndTime());
        }
        if (StringUtils.isNotBlank(param.getZkPath())) {
            wrapper.eq("zk_path", param.getZkPath());
        }
        if (StringUtils.isNotBlank(param.getValue())) {
            wrapper.eq("value", param.getValue());
        }
        if (StringUtils.isNotBlank(param.getRemark())) {
            wrapper.like("remark", param.getRemark());
        }
        final String envCode = WebPluginUtils.traceEnvCode();
        final String sysEnvCode = WebPluginUtils.SYS_DEFAULT_ENV_CODE;
        final List<String> envCodeList = Lists.newArrayList(envCode, sysEnvCode);
        final Long tenantId = WebPluginUtils.traceTenantId();
        final Long sysTenantId = WebPluginUtils.SYS_DEFAULT_TENANT_ID;
        final List<Long> tenantIdList = Lists.newArrayList(tenantId, sysTenantId);
        wrapper.in("tenant_id", tenantIdList);
        wrapper.in("env_code", envCodeList);
        wrapper.eq("is_deleted", 0);

        //1. 先获取到符合条件的zkpath
        Page<PradarZkConfigEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
        final Page<PradarZkConfigEntity> configs = pradarZkConfigMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(configs.getRecords())) {
            return PagingList.of(Lists.newArrayList(), configs.getTotal());
        }
        //2. 通过zkpath查询
        final List<PradarZkConfigEntity> zkPathList = configs.getRecords();
        final LambdaQueryWrapper<PradarZkConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PradarZkConfigEntity::getZkPath, zkPathList);
        queryWrapper.in(PradarZkConfigEntity::getTenantId, tenantIdList);
        queryWrapper.in(PradarZkConfigEntity::getEnvCode, envCodeList);
        queryWrapper.eq(PradarZkConfigEntity::getIsDeleted, 0);
        final List<PradarZkConfigEntity> list = pradarZkConfigMapper.selectList(queryWrapper);

        //3. 当前租户的配置
        Map<String, PradarZkConfigEntity> zkMap = list.stream().filter(
            t -> t.getEnvCode() != null && t.getTenantId() != null)
            .filter(t -> t.getEnvCode().equals(envCode) && t.getTenantId().equals(tenantId))
            .collect(Collectors.toMap(PradarZkConfigEntity::getZkPath, Function.identity()));
        //4. 系统配置
        Map<String, PradarZkConfigEntity> sysZkMap = list.stream().filter(
            t -> t.getEnvCode() != null && t.getTenantId() != null)
            .filter(t -> t.getEnvCode().equals(sysEnvCode) && t.getTenantId().equals(sysTenantId))
            .collect(Collectors.toMap(PradarZkConfigEntity::getZkPath, Function.identity()));

        //5. 整合
        List<PradarZKConfigResult> pradarZkConfigResultList = zkPathList.stream().filter(t->Objects.nonNull(zkMap.get(t.getZkPath())) || Objects.nonNull(sysZkMap.get(t.getZkPath()))).map(t -> {
            String zkPath = t.getZkPath();
            PradarZkConfigEntity entity = zkMap.get(t.getZkPath());
            if (Objects.isNull(entity)) {
                entity = sysZkMap.get(zkPath);
            }
            PradarZKConfigResult configResult = new PradarZKConfigResult();
            BeanUtils.copyProperties(entity, configResult);
            return configResult;
        }).collect(Collectors.toList());

        return PagingList.of(pradarZkConfigResultList, configs.getTotal());
    }

    @Override
    public List<PradarZKConfigResult> selectList() {
        LambdaQueryWrapper<PradarZkConfigEntity> wrapper = new LambdaQueryWrapper<>();
        List<PradarZkConfigEntity> result = pradarZkConfigMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        return result.stream().map(entity -> {
            PradarZKConfigResult configResult = new PradarZKConfigResult();
            BeanUtils.copyProperties(entity, configResult);
            return configResult;
        }).collect(Collectors.toList());
    }

    @Override
    public int insert(PradarConfigCreateParam createParam) {
        PradarZkConfigEntity entity = new PradarZkConfigEntity();
        BeanUtils.copyProperties(createParam, entity);
        return pradarZkConfigMapper.insert(entity);
    }

    @Override
    public int update(PradarConfigCreateParam updateParam) {
        if (!Objects.isNull(updateParam.getId())) {
            PradarZkConfigEntity entity = new PradarZkConfigEntity();
            BeanUtils.copyProperties(updateParam, entity);
            return pradarZkConfigMapper.updateById(entity);
        }
        return 0;
    }

    @Override
    public int delete(PradarConfigCreateParam deleteParam) {
        if (!Objects.isNull(deleteParam.getId())) {
            return pradarZkConfigMapper.deleteById(deleteParam.getId());
        }
        return 0;
    }

    @Override
    public PradarZKConfigResult selectById(Long id) {
        PradarZKConfigResult configResult = new PradarZKConfigResult();
        PradarZkConfigEntity entity = pradarZkConfigMapper.selectById(id);
        BeanUtils.copyProperties(entity, configResult);
        return configResult;
    }

    @Override
    public PradarZKConfigResult getByZkPath(String zkPath) {
        List<PradarZKConfigResult> configList = pradarZkConfigMapper.selectListByZkPath(zkPath,
            WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode());
        return configList.isEmpty() ? null : configList.get(0);
    }

}
