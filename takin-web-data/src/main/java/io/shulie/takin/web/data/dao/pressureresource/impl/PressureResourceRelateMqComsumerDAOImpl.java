package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateMqComsumerDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateMqConsumerMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateMqConsumerEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceMqConsumerQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
@Service
public class PressureResourceRelateMqComsumerDAOImpl implements PressureResourceRelateMqComsumerDAO {
    @Resource
    private PressureResourceRelateMqConsumerMapper pressureResourceRelateMqConsumerMapper;

    @Override
    public PagingList<PressureResourceRelateMqConsumerEntity> pageList(PressureResourceMqConsumerQueryParam param) {
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelateMqConsumerEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceRelateMqConsumerEntity> pageList = pressureResourceRelateMqConsumerMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    /**
     * 新增
     *
     * @param mqConsumerEntity
     */
    @Override
    public void add(PressureResourceRelateMqConsumerEntity mqConsumerEntity) {
        pressureResourceRelateMqConsumerMapper.insert(mqConsumerEntity);
    }

    @Override
    public List<PressureResourceRelateMqConsumerEntity> queryList(PressureResourceMqConsumerQueryParam param) {
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = this.getWrapper(param);
        List<PressureResourceRelateMqConsumerEntity> resultLists = pressureResourceRelateMqConsumerMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(resultLists)) {
            return Collections.emptyList();
        }
        return resultLists;
    }

    /**
     * 保存或更新
     *
     * @param mqConsumerEntityList
     */
    @Override
    public void saveOrUpdate(List<PressureResourceRelateMqConsumerEntity> mqConsumerEntityList) {
        if (CollectionUtils.isEmpty(mqConsumerEntityList)) {
            return;
        }
        mqConsumerEntityList.stream().forEach(entity -> {
            pressureResourceRelateMqConsumerMapper.saveOrUpdate(entity);
        });
    }

    private QueryWrapper<PressureResourceRelateMqConsumerEntity> getWrapper(PressureResourceMqConsumerQueryParam param) {
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getTopicGroup())) {
            queryWrapper.eq("topic_group", param.getTopicGroup());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (StringUtils.isNotBlank(param.getMqType())) {
            queryWrapper.eq("mq_type", param.getMqType());
        }
        if (param.getConsumerTag() != null) {
            queryWrapper.eq("consumer_tag", param.getConsumerTag());
        }
        if (StringUtils.isNotBlank(param.getQueryTopicGroup())) {
            queryWrapper.like("topic_group", param.getQueryTopicGroup());
        }
        if (StringUtils.isNotBlank(param.getQueryApplicationName())) {
            queryWrapper.like("application_name", param.getQueryApplicationName());
        }
        return queryWrapper;
    }
}
