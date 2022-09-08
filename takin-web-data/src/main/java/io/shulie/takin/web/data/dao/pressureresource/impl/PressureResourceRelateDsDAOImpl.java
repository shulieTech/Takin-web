package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateDsMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/31 7:41 PM
 */
@Service
public class PressureResourceRelateDsDAOImpl implements PressureResourceRelateDsDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRelateDsDAOImpl.class);

    @Resource
    private PressureResourceRelateDsMapper pressureResourceRelateDsMapper;

    /**
     * 批量新增
     *
     * @param dsEntitys
     */
    @Override
    public void add(List<PressureResourceRelateDsEntity> dsEntitys) {
        if (CollectionUtils.isNotEmpty(dsEntitys)) {
            dsEntitys.stream().forEach(entity -> {
                pressureResourceRelateDsMapper.insert(entity);
            });
        }
    }

    /**
     * 按参数查询
     *
     * @return
     */
    @Override
    public List<PressureResourceRelateDsEntity> queryByParam(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelateDsEntity> queryWrapper = this.getWrapper(param);
        List<PressureResourceRelateDsEntity> resultLists = pressureResourceRelateDsMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(resultLists)) {
            return Collections.emptyList();
        }
        return resultLists;
    }

    @Override
    public void saveOrUpdate(List<PressureResourceRelateDsEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        pressureResourceRelateDsMapper.saveOrUpdate(list);
    }

    private QueryWrapper<PressureResourceRelateDsEntity> getWrapper(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelateDsEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryBussinessDatabase())) {
            queryWrapper.like("business_database", param.getQueryBussinessDatabase());
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryAppName())) {
            queryWrapper.like("app_name", param.getQueryAppName());
        }
        if (StringUtils.isNotBlank(param.getBussinessDatabase())) {
            queryWrapper.eq("business_database", param.getBussinessDatabase());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        return queryWrapper;
    }
}
