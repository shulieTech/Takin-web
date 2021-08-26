package io.shulie.takin.web.data.dao.pradar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.mapper.mysql.PradarZkConfigMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigQueryParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
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

        LambdaQueryWrapper<PradarZkConfigEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            PradarZkConfigEntity::getId,
            PradarZkConfigEntity::getZkPath,
            PradarZkConfigEntity::getType,
            PradarZkConfigEntity::getValue,
            PradarZkConfigEntity::getRemark);
        if (StringUtils.isNotBlank(param.getStartTime())) {
            wrapper.ge(PradarZkConfigEntity::getCreateTime, param.getStartTime());
        }
        if (StringUtils.isNotBlank(param.getEndTime())) {
            wrapper.le(PradarZkConfigEntity::getCreateTime, param.getEndTime());
        }
        if (StringUtils.isNotBlank(param.getZkPath())) {
            wrapper.eq(PradarZkConfigEntity::getZkPath, param.getZkPath());
        }
        if (StringUtils.isNotBlank(param.getValue())) {
            wrapper.eq(PradarZkConfigEntity::getValue, param.getValue());
        }
        if (StringUtils.isNotBlank(param.getRemark())) {
            wrapper.like(PradarZkConfigEntity::getRemark, param.getRemark());
        }
        Page<PradarZkConfigEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
        wrapper.orderByDesc(PradarZkConfigEntity::getModifyTime);

        IPage<PradarZkConfigEntity> configs = pradarZkConfigMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(configs.getRecords())) {
            return PagingList.empty();
        }
        List<PradarZKConfigResult> pradarZkConfigResultList = configs.getRecords().stream().map(entity -> {
            PradarZKConfigResult configResult = new PradarZKConfigResult();
            BeanUtils.copyProperties(entity, configResult);
            return configResult;
        }).collect(Collectors.toList());

        return PagingList.of(pradarZkConfigResultList, page.getTotal());
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
}
