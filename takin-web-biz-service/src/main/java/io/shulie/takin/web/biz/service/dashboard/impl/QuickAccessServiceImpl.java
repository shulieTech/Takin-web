package io.shulie.takin.web.biz.service.dashboard.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.web.biz.pojo.response.dashboard.QuickAccessResponse;
import io.shulie.takin.web.biz.service.dashboard.QuickAccessService;
import io.shulie.takin.web.data.mapper.mysql.dashboard.QuickAccessMapper;
import io.shulie.takin.web.data.model.mysql.dashboard.QuickAccessEntity;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.stereotype.Service;

@Service
public class QuickAccessServiceImpl implements QuickAccessService {

    @Resource
    private QuickAccessMapper quickAccessMapper;

    /**
     * 列出用户设置的快捷入口
     *
     * @return 快捷入口列表
     */
    @Override
    public List<QuickAccessResponse> list() {
        // 组装查询条件

        // 执行查询
        List<QuickAccessEntity> quickAccesses = quickAccessMapper.queryList(WebPluginUtils.traceTenantId(),WebPluginUtils.traceEnvCode());
        // 组装返回值
       return quickAccesses.stream()
            .map(t -> BeanUtil.copyProperties(t, QuickAccessResponse.class, ""))
            .distinct()
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(QuickAccessResponse::getQuickName))), ArrayList::new));
    }
}
