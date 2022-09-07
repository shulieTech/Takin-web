package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
@Service
public class PressureResourceDAOImpl implements PressureResourceDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceDAOImpl.class);

    @Resource
    private PressureResourceMapper pressureResourceMapper;

    /**
     * 新增
     *
     * @param insertEntity
     */
    @Override
    public Long add(PressureResourceEntity insertEntity) {
        pressureResourceMapper.insert(insertEntity);
        return insertEntity.getId();
    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceEntity> pageList(PressureResourceQueryParam param) {
        QueryWrapper<PressureResourceEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceEntity> pageList = pressureResourceMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    @Override
    public PressureResourceEntity queryByName(String name) {
        QueryWrapper<PressureResourceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        queryWrapper.last(" limit 1");
        return pressureResourceMapper.selectOne(queryWrapper);
    }

    @Override
    public List<PressureResourceEntity> getAll() {
        return pressureResourceMapper.getAll();
    }

    private QueryWrapper<PressureResourceEntity> getWrapper(PressureResourceQueryParam param) {
        QueryWrapper<PressureResourceEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        if (param.getName() != null) {
            queryWrapper.like("name", param.getName());
        }
        if (param.getSourceId() != null) {
            queryWrapper.eq("source_id", param.getSourceId());
        }
        return queryWrapper;
    }
}
