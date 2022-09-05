package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.google.common.collect.Maps;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.*;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceDetailVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceInfoVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationAppVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDetailDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationAppDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceDetailMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceDetailEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationAppEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDetailQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
@Service
public class PressureResourceServiceImpl implements PressureResourceService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceServiceImpl.class);

    @Resource
    private PressureResourceDAO pressureResourceDAO;

    @Resource
    private PressureResourceRelationAppDAO pressureResourceRelationAppDAO;

    @Resource
    private PressureResourceMapper pressureResourceMapper;

    @Resource
    private PressureResourceDetailDAO pressureResourceDetailDAO;

    @Resource
    private PressureResourceDetailMapper pressureResourceDetailMapper;

    /**
     * 新增
     *
     * @param input
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(PressureResourceInput input) {
        if (StringUtils.isBlank(input.getName())) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "参数未传递");
        }
        PressureResourceEntity entity = pressureResourceDAO.queryByName(input.getName());
        if (entity != null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "名称已存在");
        }
        // 压测资源配置
        PressureResourceEntity insertEntity = new PressureResourceEntity();
        insertEntity.setName(input.getName());
        insertEntity.setType(input.getType());
        insertEntity.setGmtCreate(new Date());
        insertEntity.setGmtModified(new Date());
        Long resourceId = pressureResourceDAO.add(insertEntity);

        // 获取详情
        List<PressureResourceDetailInput> detailInputs = input.getDetailInputs();
        if (CollectionUtils.isNotEmpty(detailInputs)) {
            List<PressureResourceDetailEntity> insertEntityList = convertEntitys(resourceId, detailInputs);
            pressureResourceDetailDAO.batchInsert(insertEntityList);
        }
    }

    private List<PressureResourceDetailEntity> convertEntitys(Long resourceId, List<PressureResourceDetailInput> detailInputs) {
        List<PressureResourceDetailEntity> insertEntityList = detailInputs.stream().map(detail -> {
            PressureResourceDetailEntity detailEntity = new PressureResourceDetailEntity();
            BeanUtils.copyProperties(detail, detailEntity);

            detailEntity.setId(null);
            detailEntity.setResourceId(resourceId);
            detailEntity.setGmtCreate(new Date());
            detailEntity.setGmtModified(new Date());
            return detailEntity;
        }).collect(Collectors.toList());
        return insertEntityList;
    }

    /**
     * 修改
     *
     * @param input
     */
    @Override
    public void update(PressureResourceInput input) {
        if (input.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceEntity entity = pressureResourceMapper.selectById(input.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        PressureResourceQueryParam param = new PressureResourceQueryParam();
        param.setName(input.getName());
        PressureResourceEntity nameEntity = pressureResourceDAO.queryByName(input.getName());
        if (nameEntity != null && !nameEntity.getId().equals(input.getId())) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "名字已存在");
        }
        PressureResourceEntity updateResourceEntity = new PressureResourceEntity();
        updateResourceEntity.setId(input.getId());
        updateResourceEntity.setName(input.getName());
        updateResourceEntity.setGmtModified(new Date());
        pressureResourceMapper.updateById(updateResourceEntity);

        // 修改详情
        PressureResourceDetailQueryParam detailParam = new PressureResourceDetailQueryParam();
        detailParam.setResourceId(input.getId());
        List<PressureResourceDetailEntity> oldList = pressureResourceDetailDAO.getList(detailParam);
        List<PressureResourceDetailEntity> newList = convertEntitys(input.getId(), input.getDetailInputs());

        Map<String, List<PressureResourceDetailEntity>> newMap = newList.stream().collect(Collectors.groupingBy(ele -> fetchKey(ele)));
        Map<String, List<PressureResourceDetailEntity>> oldMap = oldList.stream().collect(Collectors.groupingBy(ele -> fetchKey(ele)));
        //判断需要新增的,不在oldMap里面的
        List<PressureResourceDetailEntity> insertEntitys = Lists.newArrayList();
        for (Map.Entry<String, List<PressureResourceDetailEntity>> entry : newMap.entrySet()) {
            String tmpKey = entry.getKey();
            if (!oldMap.containsKey(tmpKey)) {
                // 相同URL和请求方式只有一个
                insertEntitys.add(entry.getValue().get(0));
            }
        }
        if (CollectionUtils.isNotEmpty(insertEntitys)) {
            pressureResourceDetailDAO.batchInsert(insertEntitys);
        }
        // 删除的,不在newMap里面的
        List<Long> deleteIds = Lists.newArrayList();
        for (Map.Entry<String, List<PressureResourceDetailEntity>> entry : oldMap.entrySet()) {
            String tmpKey = entry.getKey();
            if (!newMap.containsKey(tmpKey)) {
                Long id = entry.getValue().get(0).getId();
                if (id != null) {
                    deleteIds.add(id);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            pressureResourceDetailMapper.deleteBatchIds(deleteIds);
        }
    }

    private String fetchKey(PressureResourceDetailEntity ele) {
        return String.format("%s-%s-%s-%s-%s", ele.getEntranceUrl(), ele.getMethod(), ele.getRpcType(), ele.getExtend(), ele.getAppName());
    }

    /**
     * 列表查询
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceVO> list(PressureResourceQueryRequest request) {
        PressureResourceQueryParam param = new PressureResourceQueryParam();
        BeanUtils.copyProperties(request, param);

        PagingList<PressureResourceEntity> pageList = pressureResourceDAO.pageList(param);
        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceEntity> source = pageList.getList();
        List<Long> configIds = source.stream().map(configDto -> configDto.getId()).collect(Collectors.toList());
        PressureResourceDetailQueryParam queryParam = new PressureResourceDetailQueryParam();
        queryParam.setResourceIds(configIds);
        List<PressureResourceDetailEntity> detailEntities = pressureResourceDetailDAO.getList(queryParam);
        Map<String, List<PressureResourceDetailEntity>> detailMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(detailEntities)) {
            detailMap = detailEntities.stream().collect(Collectors.groupingBy(entity -> String.valueOf(entity.getResourceId())));
        }
        Map<String, List<PressureResourceDetailEntity>> finalDetailMap = detailMap;
        List<PressureResourceVO> returnList = source.stream().map(configDto -> {
            PressureResourceVO vo = new PressureResourceVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            // 设置详情条数
            vo.setDetailCount(finalDetailMap.getOrDefault(String.valueOf(configDto.getId()), Collections.EMPTY_LIST).size());
            return vo;
        }).collect(Collectors.toList());

        return PagingList.of(returnList, pageList.getTotal());
    }

    /**
     * 详情
     *
     * @param request
     * @return
     */
    @Override
    public PressureResourceInfoVO detail(PressureResourceQueryRequest request) {
        PressureResourceInfoVO infoVO = new PressureResourceInfoVO();

        PressureResourceEntity resourceEntity = pressureResourceMapper.selectById(request.getId());
        if (resourceEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "数据不存在");
        }
        infoVO.setId(String.valueOf(resourceEntity.getId()));
        infoVO.setName(resourceEntity.getName());
        // 判断是否有链路信息
        PressureResourceDetailQueryParam param = new PressureResourceDetailQueryParam();
        param.setResourceId(request.getId());
        List<PressureResourceDetailEntity> detailList = pressureResourceDetailDAO.getList(param);
        if (CollectionUtils.isEmpty(detailList)) {
            return infoVO;
        }
        List<PressureResourceDetailVO> detailVOList = detailList.stream().map(detail -> {
            PressureResourceDetailVO tmpDetailVo = new PressureResourceDetailVO();
            BeanUtils.copyProperties(detail, tmpDetailVo);
            tmpDetailVo.setValue(String.valueOf(detail.getId()));
            return tmpDetailVo;
        }).collect(Collectors.toList());
        infoVO.setDetailInputs(detailVOList);
        return infoVO;
    }

    /**
     * 修改隔离方式
     *
     * @param isolateInput
     */
    @Override
    public void updateIsolate(PressureResourceIsolateInput isolateInput) {
        if (isolateInput.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceEntity entity = pressureResourceMapper.selectById(isolateInput.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "未查询到指定数据");
        }
        // 修改主表隔离方式
        PressureResourceEntity updateEntity = new PressureResourceEntity();
        updateEntity.setId(entity.getId());
        updateEntity.setIsolateType(isolateInput.getIsolateType());
        updateEntity.setGmtModified(new Date());
    }
}
