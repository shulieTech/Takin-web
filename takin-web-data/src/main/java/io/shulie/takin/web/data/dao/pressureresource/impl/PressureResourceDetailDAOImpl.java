package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDetailDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceDetailMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceDetailEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDetailQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 6:59 PM
 */
@Service
public class PressureResourceDetailDAOImpl implements PressureResourceDetailDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceDAOImpl.class);

    @Resource
    private PressureResourceDetailMapper pressureResourceDetailMapper;

    @Override
    public List<PressureResourceDetailEntity> getList(PressureResourceDetailQueryParam param) {
        QueryWrapper<PressureResourceDetailEntity> queryWrapper = this.getWrapper(param);
        return pressureResourceDetailMapper.selectList(queryWrapper);
    }

    /**
     * 批量新增
     *
     * @param insertList
     */
    @Override
    public void batchInsert(List<PressureResourceDetailEntity> insertList) {
        if (CollectionUtils.isEmpty(insertList)) {
            return;
        }
        insertList.stream().forEach(insert -> {
            pressureResourceDetailMapper.insert(insert);
        });
    }

    private QueryWrapper<PressureResourceDetailEntity> getWrapper(PressureResourceDetailQueryParam param) {
        QueryWrapper<PressureResourceDetailEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resourceId", param.getResourceId());
        }
        return queryWrapper;
    }
}
