package io.shulie.takin.web.data.dao.pradar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.mapper.mysql.PradarZkConfigMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.param.pradarconfig.PagePradarZkConfigParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigQueryParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
import io.shulie.takin.web.data.util.MPUtil;
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
public class PradarZkConfigDAOImpl implements PradarZkConfigDAO, MPUtil<PradarZkConfigEntity> {

    @Autowired
    private PradarZkConfigMapper pradarZkConfigMapper;

    @Override
    public PagingList<PradarZKConfigResult> selectPage(PradarConfigQueryParam param) {
        QueryWrapper<PradarZkConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.ge(StringUtils.isNotBlank(param.getStartTime()), "create_time", param.getStartTime())
            .le(StringUtils.isNotBlank(param.getEndTime()), "create_time", param.getEndTime())
            .eq(StringUtils.isNotBlank(param.getZkPath()), "zk_path", param.getZkPath())
            .eq(StringUtils.isNotBlank(param.getValue()), "value", param.getValue())
            .like(StringUtils.isNotBlank(param.getRemark()), "remark", param.getRemark())
            .select("zk_path")
            .eq("tenant_id", WebPluginUtils.traceTenantId())
            .eq("env_code", WebPluginUtils.traceEnvCode());

        //1. 先获取到符合条件的zkpath
        IPage<PradarZkConfigEntity> page = new Page<>(param.getRealCurrent(), param.getPageSize());
        IPage<PradarZkConfigEntity> configs = pradarZkConfigMapper.selectPage(page, wrapper);
        List<PradarZkConfigEntity> zkPathEntityList = configs.getRecords();
        if (CollectionUtils.isEmpty(zkPathEntityList)) {
            return PagingList.of(new ArrayList<>(0), configs.getTotal());
        }

        //2. 通过zkpath查询
        List<String> zkPathList = zkPathEntityList.stream().map(PradarZkConfigEntity::getZkPath)
            .filter(StrUtil::isNotBlank).collect(Collectors.toList());
        LambdaQueryWrapper<PradarZkConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PradarZkConfigEntity::getZkPath, zkPathList);
        queryWrapper.in(PradarZkConfigEntity::getTenantId, tenantIdList);
        queryWrapper.in(PradarZkConfigEntity::getEnvCode, envCodeList);
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
        List<PradarZKConfigResult> pradarZkConfigResultList = zkPathList.stream().filter(zkPath->Objects.nonNull(zkMap.get(zkPath)) || Objects.nonNull(sysZkMap.get(zkPath))).map(zkPath -> {
            PradarZkConfigEntity entity = zkMap.get(zkPath);
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
    public List<PradarZKConfigResult> listSystemConfig() {
        List<PradarZkConfigEntity> result = pradarZkConfigMapper.selectList(this.getLambdaQueryWrapper()
            .select(PradarZkConfigEntity::getZkPath, PradarZkConfigEntity::getValue)
            .eq(PradarZkConfigEntity::getTenantId, WebPluginUtils.SYS_DEFAULT_TENANT_ID)
            .eq(PradarZkConfigEntity::getEnvCode, WebPluginUtils.SYS_DEFAULT_ENV_CODE));
        return CommonUtil.list2list(result, PradarZKConfigResult.class);
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
        return CommonUtil.copyBeanPropertiesWithNull(pradarZkConfigMapper.selectById(id), PradarZKConfigResult.class);
    }

    @Override
    public PradarZKConfigResult getByZkPath(String zkPath) {
        List<PradarZKConfigResult> configList = pradarZkConfigMapper.selectListByZkPath(zkPath,
            WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode());
        return configList.isEmpty() ? null : configList.get(0);
    }

    @Override
    public IPage<PradarZKConfigResult> page(PagePradarZkConfigParam param, PageBaseDTO pageBaseDTO) {
        return pradarZkConfigMapper.selectPageByTenantIdAndEnvCode(this.setPage(pageBaseDTO), param);
    }

}
