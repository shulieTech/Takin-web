package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationDsDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationDsMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationDsEntity;
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
public class PressureResourceRelationDsDAOImpl implements PressureResourceRelationDsDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRelationDsDAOImpl.class);

    @Resource
    private PressureResourceRelationDsMapper pressureResourceRelationDsMapper;

    /**
     * 批量新增
     *
     * @param dsEntitys
     */
    @Override
    public void add(List<PressureResourceRelationDsEntity> dsEntitys) {
        if (CollectionUtils.isNotEmpty(dsEntitys)) {
            dsEntitys.stream().forEach(entity -> {
                pressureResourceRelationDsMapper.insert(entity);
            });
        }
    }

    /**
     * 按参数查询
     *
     * @return
     */
    @Override
    public List<PressureResourceRelationDsEntity> queryByParam(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelationDsEntity> queryWrapper = this.getWrapper(param);
        List<PressureResourceRelationDsEntity> resultLists = pressureResourceRelationDsMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(resultLists)) {
            return Collections.emptyList();
        }
        return resultLists;
    }

    private QueryWrapper<PressureResourceRelationDsEntity> getWrapper(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelationDsEntity> queryWrapper = new QueryWrapper<>();
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
