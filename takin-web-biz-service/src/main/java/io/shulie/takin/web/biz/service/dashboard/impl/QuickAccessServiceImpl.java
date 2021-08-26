package io.shulie.takin.web.biz.service.dashboard.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.web.biz.service.dashboard.QuickAccessService;
import io.shulie.takin.web.data.model.mysql.dashboard.QuickAccessEntity;
import io.shulie.takin.web.data.mapper.mysql.dashboard.QuickAccessMapper;
import io.shulie.takin.web.biz.pojo.response.dashboard.QuickAccessResponse;

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
        QueryWrapper<QuickAccessEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("`order`");

        // 执行查询
        List<QuickAccessEntity> quickAccesses = quickAccessMapper.selectList(queryWrapper);
        // 组装返回值
        return quickAccesses.stream()
            .map(t -> BeanUtil.copyProperties(t, QuickAccessResponse.class, ""))
            .collect(Collectors.toList());
    }
}
