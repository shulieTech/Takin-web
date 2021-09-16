package io.shulie.takin.web.data.dao.blacklist;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import io.shulie.takin.common.beans.page.PagingList;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.collections4.CollectionUtils;
import io.shulie.takin.web.common.vo.blacklist.BlacklistVO;
import io.shulie.takin.web.data.model.mysql.BlackListEntity;
import io.shulie.takin.web.data.mapper.mysql.BlackListMapper;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.web.data.param.blacklist.BlackListCreateParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistSearchParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistUpdateParam;
import io.shulie.takin.web.common.enums.blacklist.BlacklistEnableEnum;
import io.shulie.takin.web.data.param.blacklist.BlacklistCreateNewParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

/**
 * 黑名单dao
 *
 * @author fanxx
 * @date 2020/11/4 8:18 下午
 */
@Component
public class BlackListDAOImpl implements BlackListDAO {

    @Resource
    private BlackListMapper blackListMapper;

    @Override
    public int insert(BlackListCreateParam param) {
        BlackListEntity entity = new BlackListEntity();
        entity.setRedisKey(param.getRedisKey());
        entity.setUseYn(param.getUseYn());
        entity.setGmtCreate(param.getCreateTime());
        entity.setGmtModified(param.getUpdateTime());
        return blackListMapper.insert(entity);
    }

    @Override
    public void newInsert(BlacklistCreateNewParam param) {
        BlackListEntity entity = new BlackListEntity();
        BeanUtils.copyProperties(param, entity);
        blackListMapper.insert(entity);
    }

    @Override
    public void batchInsert(List<BlacklistCreateNewParam> params) {
        params.forEach(param -> {
            BlackListEntity entity = new BlackListEntity();
            BeanUtils.copyProperties(param, entity);
            blackListMapper.insert(entity);
        });

    }

    @Override
    public void update(BlacklistUpdateParam param) {
        BlackListEntity entity = new BlackListEntity();
        BeanUtils.copyProperties(param, entity);
        blackListMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        blackListMapper.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        blackListMapper.deleteBatchIds(ids);
    }

    @Override
    public void logicalDelete(List<Long> ids) {
        LambdaUpdateWrapper<BlackListEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(BlackListEntity::getBlistId, ids);
        wrapper.set(BlackListEntity::getIsDeleted, true);
        blackListMapper.update(null, wrapper);
    }

    @Override
    public PagingList<BlacklistVO> pageList(BlacklistSearchParam param) {
        LambdaQueryWrapper<BlackListEntity> wrapper = getBlackListEntityLambdaQueryWrapper(param);
        Page<BlackListEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        wrapper.orderByDesc(BlackListEntity::getGmtModified, BlackListEntity::getBlistId);
        if (StringUtils.isNotBlank(param.getRedisKey())) {
            wrapper.like(BlackListEntity::getRedisKey, param.getRedisKey());
        }
        IPage<BlackListEntity> infoEntityPageInfo = blackListMapper.selectPage(page, wrapper);
        if (infoEntityPageInfo.getRecords().isEmpty()) {
            return PagingList.of(Lists.newArrayList(), infoEntityPageInfo.getTotal());
        }
        List<BlacklistVO> results = infoEntityPageInfo.getRecords().stream().map(entity -> {
            BlacklistVO result = new BlacklistVO();
            BeanUtils.copyProperties(entity, result);
            result.setGmtCreate(DateUtil.formatDateTime(entity.getGmtCreate()));
            result.setGmtModified(DateUtil.formatDateTime(entity.getGmtModified()));
            return result;
        }).collect(Collectors.toList());
        return PagingList.of(results, infoEntityPageInfo.getTotal());
    }

    @Override
    public List<BlacklistResult> selectList(BlacklistSearchParam param) {
        LambdaQueryWrapper<BlackListEntity> wrapper = getBlackListEntityLambdaQueryWrapper(param);
        if (StringUtils.isNotBlank(param.getRedisKey())) {
            wrapper.eq(BlackListEntity::getRedisKey, param.getRedisKey());
        }
        List<BlackListEntity> entities = blackListMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> {
            BlacklistResult result = new BlacklistResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<BlackListEntity> getBlackListEntityLambdaQueryWrapper(BlacklistSearchParam param) {
        LambdaQueryWrapper<BlackListEntity> wrapper = new LambdaQueryWrapper<>();
        if (param.getApplicationId() != null) {
            wrapper.eq(BlackListEntity::getApplicationId, param.getApplicationId());
        }
        // 数据权限放开
        //if (param.getCustomerId() != null) {
        //    wrapper.eq(BlackListEntity::getCustomerId, param.getCustomerId());
        //}
        //if (param.getUserId() != null) {
        //    wrapper.eq(BlackListEntity::getUserId, param.getUserId());
        //}
        wrapper.eq(BlackListEntity::getIsDeleted, false);
        return wrapper;
    }

    @Override
    public BlacklistResult selectById(Long id) {
        BlackListEntity entity = blackListMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        BlacklistResult result = new BlacklistResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }

    @Override
    public List<BlacklistResult> selectByIds(List<Long> ids) {
        LambdaQueryWrapper<BlackListEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlackListEntity::getBlistId, ids);
        List<BlackListEntity> entities = blackListMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> {
            BlacklistResult result = new BlacklistResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BlacklistResult> getAllEnabledBlockList(Long applicationId) {
        LambdaQueryWrapper<BlackListEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlackListEntity::getIsDeleted, false);
        wrapper.in(BlackListEntity::getUseYn, BlacklistEnableEnum.ENABLE.getStatus());

        if (applicationId != null) {
            wrapper.eq(BlackListEntity::getApplicationId, applicationId);
        }
        List<BlackListEntity> entities = blackListMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> {
            BlacklistResult result = new BlacklistResult();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
    }
}
