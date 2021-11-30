package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.entity.TPradaHttpData;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.query.TWListVo;
import com.pamirs.takin.entity.domain.vo.TApplicationInterface;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceDataVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.vo.whitelist.WhiteListVO;
import io.shulie.takin.web.data.dao.application.WhiteListDAO;
import io.shulie.takin.web.data.mapper.mysql.WhiteListMapper;
import io.shulie.takin.web.data.model.mysql.WhiteListEntity;
import io.shulie.takin.web.data.param.whitelist.WhitelistGlobalOrPartParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSaveOrUpdateParam;
import io.shulie.takin.web.data.param.whitelist.WhitelistSearchParam;
import io.shulie.takin.web.data.result.whitelist.WhitelistResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author liuchuan
 * @date 2021/4/8 2:11 下午
 */
@Service
public class WhiteListDAOImpl extends ServiceImpl<WhiteListMapper, WhiteListEntity>
        implements WhiteListDAO, MPUtil<WhiteListEntity> {

    @Override
    public void batchSaveOrUpdate(List<WhitelistSaveOrUpdateParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        List<WhiteListEntity> entities = params.stream().map(param -> {
            WhiteListEntity entity = new WhiteListEntity();
            BeanUtils.copyProperties(param, entity);
            return entity;
        }).collect(Collectors.toList());
        this.saveOrUpdateBatch(entities);
    }


    @Override
    public List<WhitelistResult> listByApplicationId(Long applicationId) {
        List<WhiteListEntity> entities = this.list(
                this.getLambdaQueryWrapper().eq(WhiteListEntity::getApplicationId, applicationId).eq(WhiteListEntity::getIsDeleted, false));
        return getWhitelistResults(entities);
    }

    private List<WhitelistResult> getWhitelistResults(List<WhiteListEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> {
            WhitelistResult result = new WhitelistResult();
            BeanUtil.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public List<WhitelistResult> getList(WhitelistSearchParam param) {
        LambdaQueryWrapper<WhiteListEntity> wrapper = getWhiteListEntityLambdaQueryWrapper(param);
        List<WhiteListEntity> entities = this.list(wrapper);
        return getWhitelistResults(entities);
    }

    @Override
    public WhitelistResult selectById(Long wlistId) {
        WhiteListEntity entity = this.getById(wlistId);
        if (entity == null) {
            return null;
        }
        WhitelistResult result = new WhitelistResult();
        BeanUtil.copyProperties(entity, result);
        return result;
    }

    @Override
    public PagingList<WhiteListVO> pagingList(WhitelistSearchParam param) {
        LambdaQueryWrapper<WhiteListEntity> wrapper = this.getWhiteListEntityLambdaQueryWrapper(param);
        wrapper.orderByDesc(WhiteListEntity::getGmtModified);
        Page<WhiteListEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        IPage<WhiteListEntity> entityPageInfo = this.page(page, wrapper);
        if (CollectionUtils.isEmpty(entityPageInfo.getRecords())) {
            return PagingList.of(Lists.newArrayList(),entityPageInfo.getTotal());
        }
        List<WhiteListVO> vos = entityPageInfo.getRecords().stream().map(entity -> {
            WhiteListVO vo = new WhiteListVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setApplicationId(String.valueOf(entity.getApplicationId()));
            vo.setWlistId(String.valueOf(entity.getWlistId()));
            vo.setDbId(String.valueOf(entity.getWlistId()));
            vo.setInterfaceType(Integer.valueOf(entity.getType()));
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(vos, entityPageInfo.getTotal());
    }

    private LambdaQueryWrapper<WhiteListEntity> getWhiteListEntityLambdaQueryWrapper(WhitelistSearchParam param) {
        LambdaQueryWrapper<WhiteListEntity> wrapper = this.getLambdaQueryWrapper();
        if (CollectionUtil.isNotEmpty(param.getIds())) {
            wrapper.in(WhiteListEntity::getApplicationId, param.getIds());
        }
        if (StringUtils.isNotBlank(param.getInterfaceName())) {
            wrapper.like(WhiteListEntity::getInterfaceName, param.getInterfaceName());
        }
        if (param.getTenantId() != null) {
            wrapper.eq(WhiteListEntity::getTenantId, param.getTenantId());
        }
        if (StringUtils.isNotBlank(param.getEnvCode())) {
            wrapper.eq(WhiteListEntity::getEnvCode, param.getEnvCode());
        }
        if (param.getIsGlobal() != null) {
            wrapper.eq(WhiteListEntity::getIsGlobal, param.getIsGlobal());
        }
        if (param.getUseYn() != null) {
            wrapper.eq(WhiteListEntity::getUseYn, param.getUseYn());
        }
        wrapper.eq(WhiteListEntity::getIsDeleted, false);
        return wrapper;
    }

    @Override
    public void updateWhitelistGlobal(WhitelistGlobalOrPartParam param) {
        WhiteListEntity entity = new WhiteListEntity();
        BeanUtils.copyProperties(param, entity);
        this.updateById(entity);
    }


    // ----- 迁移

    @Override
    public void deleteApplicationInfoRelatedInterfaceByIds(List<String> applicationIdLists) {
        this.baseMapper.deleteApplicationInfoRelatedInterfaceByIds(applicationIdLists);
    }

    @Override
    public int whiteListExist(String appId, String interfaceName, String useYn) {
       return this.baseMapper.whiteListExist(appId,interfaceName,useYn);
    }

    @Override
    public int batchEnableWhiteList(List<Long> whiteListIdList) {
        return this.baseMapper.batchEnableWhiteList(whiteListIdList);
    }

    @Override
    public int batchDisableWhiteList(List<Long> whiteListIdList) {
        return this.baseMapper.batchDisableWhiteList(whiteListIdList);
    }

    @Override
    public void addWhiteList(TWList tWhiteList) {
        this.baseMapper.addWhiteList(tWhiteList);
    }

    @Override
    public TWList querySingleWhiteListById(String wlistId) {
        return this.baseMapper.querySingleWhiteListById(wlistId);
    }

    @Override
    public void updateSelective(TWList tWhiteList) {
        this.baseMapper.updateSelective(tWhiteList);
    }

    @Override
    public void deleteWhiteListByIds(List<String> whitelistIds) {
        this.baseMapper.deleteWhiteListByIds(whitelistIds);
    }

    @Override
    public void deleteByIds(List<Long> whiteListIds) {
        this.baseMapper.deleteByIds(whiteListIds);
    }

    @Override
    public List<TWList> queryWhiteListByIds(List<String> whiteListIds) {
        return this.baseMapper.queryWhiteListByIds(whiteListIds);
    }

    @Override
    public List<TWList> getWhiteListByIds(List<Long> whiteListIds) {
        return this.baseMapper.getWhiteListByIds(whiteListIds);
    }

    @Override
    public List<Map<String, Object>> queryWhiteListList(String applicationName) {
        return this.baseMapper.queryWhiteListList(applicationName);
    }

    @Override
    public List<TApplicationInterface> queryOnlyWhiteList(String applicationName, String principalNo, String type, String whiteListUrl,
        List<String> wlistIds, Long applicationId) {
        return this.baseMapper.queryOnlyWhiteList(applicationName,principalNo,type,whiteListUrl,wlistIds,applicationId);
    }

    @Override
    public List<TWList> queryWhiteListTotalByApplicationId(Long applicationId) {
        return this.baseMapper.queryDistinctWhiteListTotalByApplicationId(applicationId);
    }

    @Override
    public List<TWList> queryDistinctWhiteListTotalByApplicationId(Long applicationId) {
        return this.baseMapper.queryDistinctWhiteListTotalByApplicationId(applicationId);
    }

    @Override
    public Map<String, Object> queryWhiteListRelationBasicLinkByWhiteListId(String wListId) {
        return this.baseMapper.queryWhiteListRelationBasicLinkByWhiteListId(wListId);
    }

    @Override
    public List<TPradaHttpData> queryInterfaceByAppNameByTPHD(String applicationName, String type, String interfaceName) {
        return this.baseMapper.queryInterfaceByAppNameByTPHD(applicationName,type,interfaceName);
    }

    @Override
    public List<TUploadInterfaceDataVo> queryInterfaceByAppNameFromTUID(String applicationName, String type, String interfaceName) {
        return this.baseMapper.queryInterfaceByAppNameFromTUID(applicationName,type,interfaceName);
    }

    @Override
    public void batchAddWhiteList(List<TWList> twLists) {
        this.baseMapper.batchAddWhiteList(twLists);
    }

    @Override
    public int queryWhiteListCountByMqInfo(TWListVo twListVo) {
        return this.baseMapper.queryWhiteListCountByMqInfo(twListVo);
    }

    @Override
    public List<Map<String, String>> getWhiteListForLink() {
        return this.baseMapper.getWhiteListForLink();
    }

    @Override
    public List<Map<String, Object>> queryWhiteListByAppId(String applicationId) {
        return this.baseMapper.queryWhiteListByAppId(applicationId);
    }

    @Override
    public TWList getWhiteListByParam(Map<String, String> queryMap) {
        return this.baseMapper.getWhiteListByParam(queryMap);
    }

    @Override
    public List<Map<String, Object>> getWhiteListByAppIds(List<String> ids) {
        return this.baseMapper.getWhiteListByAppIds(ids);
    }

    @Override
    public List<TWList> getWhiteListByApplicationId(Long applicationId) {
        return this.baseMapper.getWhiteListByApplicationId(applicationId);
    }

    @Override
    public List<TWList> getAllEnableWhitelists(String applicationId) {
        return this.baseMapper.getAllEnableWhitelists(applicationId);
    }
}