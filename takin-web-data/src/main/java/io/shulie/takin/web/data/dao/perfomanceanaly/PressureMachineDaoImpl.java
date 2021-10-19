package io.shulie.takin.web.data.dao.perfomanceanaly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.mapper.mysql.PressureMachineMapper;
import io.shulie.takin.web.data.model.mysql.PressureMachineEntity;
import io.shulie.takin.web.data.param.machine.PressureMachineDeleteParam;
import io.shulie.takin.web.data.param.machine.PressureMachineInsertParam;
import io.shulie.takin.web.data.param.machine.PressureMachineQueryParam;
import io.shulie.takin.web.data.param.machine.PressureMachineUpdateParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mubai
 * @date 2020-11-12 21:00
 */

@Component
public class PressureMachineDaoImpl implements PressureMachineDao {

    @Resource
    private PressureMachineMapper pressureMachineMapper;

    @Override
    public Integer insert(PressureMachineInsertParam param) {
        PressureMachineEntity entity = new PressureMachineEntity();
        BeanUtils.copyProperties(param, entity);
        return pressureMachineMapper.insert(entity);
    }

    @Override
    public Long getCountByIp(String ip) {
        LambdaQueryWrapper<PressureMachineEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PressureMachineEntity::getIp, ip);
        return pressureMachineMapper.selectCount(wrapper);
    }

    @Override
    public void update(PressureMachineUpdateParam param) {
        PressureMachineEntity entity = new PressureMachineEntity();
        BeanUtils.copyProperties(param, entity);
        pressureMachineMapper.updateById(entity);
    }

    @Override
    public void delete(PressureMachineDeleteParam param) {
        pressureMachineMapper.deleteById(param.getId());
    }

    @Override
    public PressureMachineResult getById(Long id) {
        PressureMachineEntity entity = pressureMachineMapper.selectById(id);
        PressureMachineResult result = new PressureMachineResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public PressureMachineResult getByIp(String ip) {
        PressureMachineEntity entity = pressureMachineMapper.getByIp(ip);
        if (entity == null) {
            return null;
        }
        PressureMachineResult result = new PressureMachineResult();
        BeanUtils.copyProperties(entity, result);
        return result;

    }

    @Override
    public PagingList<PressureMachineResult> queryByExample(PressureMachineQueryParam queryParam) {
        LambdaQueryWrapper<PressureMachineEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
            PressureMachineEntity::getId,
            PressureMachineEntity::getName,
            PressureMachineEntity::getIp,
            PressureMachineEntity::getCpu,
            PressureMachineEntity::getMemory,
            PressureMachineEntity::getMemoryUsed,
            PressureMachineEntity::getCpuLoad,
            PressureMachineEntity::getCpuUsage,
            PressureMachineEntity::getDiskIoWait,
            PressureMachineEntity::getDisk,
            PressureMachineEntity::getFlag,
            PressureMachineEntity::getGmtCreate,
            PressureMachineEntity::getGmtUpdate,
            PressureMachineEntity::getTransmittedTotal,
            PressureMachineEntity::getTransmittedIn,
            PressureMachineEntity::getTransmittedOut,
            PressureMachineEntity::getTransmittedUsage,
            PressureMachineEntity::getStatus,
            PressureMachineEntity::getMachineUsage,
            PressureMachineEntity::getSceneNames

        );
        if (queryParam.getId() != null) {
            wrapper.eq(PressureMachineEntity::getId, queryParam.getId());
        }
        if (queryParam.getFlag() != null) {
            wrapper.like(PressureMachineEntity::getFlag, queryParam.getFlag());
        }
        if (queryParam.getIp() != null) {
            wrapper.like(PressureMachineEntity::getIp, queryParam.getIp());
        }
        if (queryParam.getName() != null) {
            wrapper.like(PressureMachineEntity::getName, queryParam.getName());
        }
        if (queryParam.getStatus() != null) {
            wrapper.eq(PressureMachineEntity::getStatus, queryParam.getStatus());
        }
        if (queryParam.getMachineUsageOrder() != null) {
            Integer order = queryParam.getMachineUsageOrder();
            if (order == 1) {
                wrapper.orderByAsc(PressureMachineEntity::getMachineUsage);
            } else if (order == -1) {
                wrapper.orderByDesc(PressureMachineEntity::getMachineUsage);
            }
        } else {
            wrapper.orderByDesc(PressureMachineEntity::getGmtCreate);
        }

        Page<PressureMachineEntity> page = new Page<>(queryParam.getCurrent(), queryParam.getPageSize());
        Page<PressureMachineEntity> pressureMachineEntityPage = pressureMachineMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(pressureMachineEntityPage.getRecords())) {
            return PagingList.of(Lists.newArrayList(),pressureMachineEntityPage.getTotal());
        }
        List<PressureMachineResult> pressureMachineResultList = pressureMachineEntityPage.getRecords().stream().map(
            pressureMachineEntity -> {
                PressureMachineResult result = new PressureMachineResult();
                BeanUtils.copyProperties(pressureMachineEntity, result);
                return result;
            }).collect(Collectors.toList());

        return PagingList.of(pressureMachineResultList, pressureMachineEntityPage.getTotal());
    }
}
