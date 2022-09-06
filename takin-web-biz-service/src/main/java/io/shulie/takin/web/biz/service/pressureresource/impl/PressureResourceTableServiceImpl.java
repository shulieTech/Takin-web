package io.shulie.takin.web.biz.service.pressureresource.impl;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationTableInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationTableRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceTableService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationTableVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationTableDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationTableMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/1 8:36 PM
 */
@Service
public class PressureResourceTableServiceImpl implements PressureResourceTableService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceTableServiceImpl.class);

    @Resource
    private PressureResourceRelationTableDAO pressureResourceRelationTableDAO;

    @Resource
    private PressureResourceRelationTableMapper pressureResourceRelationTableMapper;

    /**
     * 新增
     *
     * @param input
     */
    @Override
    public void save(PressureResourceRelationTableInput input) {
        // 判断业务表是否存在
        PressureResourceTableQueryParam queryParam = new PressureResourceTableQueryParam();
        queryParam.setBusinessTableName(input.getBusinessTable());
        queryParam.setDsId(input.getDsId());
        List<PressureResourceRelationTableEntity> tableList = pressureResourceRelationTableDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(tableList)) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "业务表已存在");
        }
        // 新增
        PressureResourceRelationTableEntity tableEntity = new PressureResourceRelationTableEntity();
        tableEntity.setResourceId(input.getResourceId());
        tableEntity.setDsId(input.getDsId());
        tableEntity.setBusinessTable(input.getBusinessTable());
        tableEntity.setShadowTable(input.getShadowTable());
        tableEntity.setJoinFlag(input.getJoinFlag());
        tableEntity.setType(input.getType());
        tableEntity.setGmtCreate(new Date());
        pressureResourceRelationTableDAO.add(Arrays.asList(tableEntity));
    }

    /**
     * update
     *
     * @param updateInput
     */
    @Override
    public void update(PressureResourceRelationTableInput updateInput) {
        if (updateInput.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelationTableEntity entity = pressureResourceRelationTableMapper.selectById(updateInput.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        PressureResourceRelationTableRequest tableRequest = new PressureResourceRelationTableRequest();
        tableRequest.setBusinessTableName(updateInput.getBusinessTable());
        PagingList<PressureResourceRelationTableVO> pageList = this.pageList(tableRequest);
        if (!pageList.isEmpty()) {
            List<PressureResourceRelationTableVO> list = pageList.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                PressureResourceRelationTableVO vo = list.get(0);
                if (!vo.getId().equals(String.valueOf(updateInput.getId()))) {
                    throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "业务表名已存在");
                }
            }
        }
        PressureResourceRelationTableEntity updateEntity = new PressureResourceRelationTableEntity();
        updateEntity.setId(updateInput.getId());
        updateEntity.setJoinFlag(updateInput.getJoinFlag());
        updateEntity.setShadowTable(updateInput.getShadowTable());
        updateEntity.setRemark(updateEntity.getRemark());
        updateEntity.setStatus(updateEntity.getStatus());
        updateEntity.setExtInfo(updateEntity.getExtInfo());
        pressureResourceRelationTableMapper.updateById(updateEntity);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        pressureResourceRelationTableMapper.deleteById(id);
    }

    /**
     * 分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationTableVO> pageList(PressureResourceRelationTableRequest request) {
        PressureResourceTableQueryParam param = new PressureResourceTableQueryParam();
        BeanUtils.copyProperties(request, param);
        PagingList<PressureResourceRelationTableEntity> pageList = pressureResourceRelationTableDAO.pageList(param);

        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelationTableEntity> source = pageList.getList();
        List<PressureResourceRelationTableVO> returnList = source.stream().map(configDto -> {
            PressureResourceRelationTableVO vo = new PressureResourceRelationTableVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, pageList.getTotal());
    }
}
