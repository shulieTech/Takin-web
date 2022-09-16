package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceRemoteCallQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/14 11:09 AM
 */
@Service
public class PressureResourceRelateRemoteCallDAOImpl implements PressureResourceRelateRemoteCallDAO {
    @Resource
    private PressureResourceRelateRemoteCallMapper pressureResourceRelateRemoteCallMapper;

    /**
     * 存在则更新
     *
     * @param remoteCallEntityList
     */
    @Override
    public void saveOrUpdate(List<PressureResourceRelateRemoteCallEntity> remoteCallEntityList) {
        if (CollectionUtils.isEmpty(remoteCallEntityList)) {
            return;
        }
        remoteCallEntityList.stream().forEach(remote -> {
            pressureResourceRelateRemoteCallMapper.saveOrUpdate(remote);
        });
    }

    /**
     * 分页
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateRemoteCallEntity> pageList(PressureResourceRemoteCallQueryParam param) {
        QueryWrapper<PressureResourceRelateRemoteCallEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelateRemoteCallEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByAsc("rpc_id");
        IPage<PressureResourceRelateRemoteCallEntity> pageList = pressureResourceRelateRemoteCallMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    private QueryWrapper<PressureResourceRelateRemoteCallEntity> getWrapper(PressureResourceRemoteCallQueryParam param) {
        QueryWrapper<PressureResourceRelateRemoteCallEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryInterfaceName())) {
            queryWrapper.like("interface_name", param.getQueryInterfaceName());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (param.getInterfaceChildType() != null) {
            queryWrapper.eq("interface_child_type", param.getInterfaceChildType());
        }
        if (param.getPass() != null) {
            queryWrapper.eq("pass", param.getPass());
        }
        if (param.getStatus() != null) {
            queryWrapper.eq("status", param.getStatus());
        }
        return queryWrapper;
    }
}
