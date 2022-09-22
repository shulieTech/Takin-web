package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateTableInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateTableRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceDsService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceTableService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelateTableVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateTableDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateTableMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
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
    private PressureResourceRelateTableDAO pressureResourceRelateTableDAO;

    @Resource
    private PressureResourceRelateTableMapper pressureResourceRelateTableMapper;

    @Resource
    private PressureResourceDsService pressureResourceDsService;

    /**
     * 新增
     *
     * @param input
     */
    @Override
    public void save(PressureResourceRelateTableInput input) {
        // 判断业务表是否存在
        PressureResourceTableQueryParam queryParam = new PressureResourceTableQueryParam();
        queryParam.setBusinessTableName(input.getBusinessTable());
        String dsKey = pressureResourceDsService.getDsKey(input.getDsId());
        queryParam.setDsKey(dsKey);
        List<PressureResourceRelateTableEntity> tableList = pressureResourceRelateTableDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(tableList)) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "业务表已存在");
        }
        // 新增
        PressureResourceRelateTableEntity tableEntity = new PressureResourceRelateTableEntity();
        tableEntity.setResourceId(input.getResourceId());
        tableEntity.setBusinessTable(input.getBusinessTable());
        tableEntity.setShadowTable(input.getShadowTable());
        tableEntity.setJoinFlag(input.getJoinFlag());
        tableEntity.setType(input.getType());
        tableEntity.setGmtCreate(new Date());
        tableEntity.setDsKey(dsKey);
        pressureResourceRelateTableDAO.add(Arrays.asList(tableEntity));
    }

    /**
     * update
     *
     * @param updateInput
     */
    @Override
    public void update(PressureResourceRelateTableInput updateInput) {
        if (updateInput.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelateTableEntity entity = pressureResourceRelateTableMapper.selectById(updateInput.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        PressureResourceRelateTableRequest tableRequest = new PressureResourceRelateTableRequest();
        tableRequest.setBusinessTableName(updateInput.getBusinessTable());
        PagingList<PressureResourceRelateTableVO> pageList = this.pageList(tableRequest);
        if (!pageList.isEmpty()) {
            List<PressureResourceRelateTableVO> list = pageList.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                PressureResourceRelateTableVO vo = list.get(0);
                if (!vo.getId().equals(String.valueOf(updateInput.getId()))) {
                    throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "业务表名已存在");
                }
            }
        }
        PressureResourceRelateTableEntity updateEntity = new PressureResourceRelateTableEntity();
        updateEntity.setId(updateInput.getId());
        updateEntity.setJoinFlag(updateInput.getJoinFlag());
        updateEntity.setBusinessTable(updateInput.getBusinessTable());
        updateEntity.setShadowTable(updateInput.getShadowTable());
        updateEntity.setRemark(updateEntity.getRemark());
        updateEntity.setStatus(updateEntity.getStatus());
        updateEntity.setExtInfo(updateEntity.getExtInfo());
        pressureResourceRelateTableMapper.updateById(updateEntity);
    }

    /**
     * 批量加入或取消
     *
     * @param updateInput
     */
    @Override
    public void batchUpdate(PressureResourceRelateTableInput updateInput) {
        if (CollectionUtils.isEmpty(updateInput.getIds())) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "参数未传递");
        }
        PressureResourceRelateTableEntity updateEntity = new PressureResourceRelateTableEntity();
        updateEntity.setJoinFlag(updateInput.getJoinFlag());

        QueryWrapper<PressureResourceRelateTableEntity> updateWrapper = new QueryWrapper<>();
        updateWrapper.in("id", updateInput.getIds());
        pressureResourceRelateTableMapper.update(updateEntity, updateWrapper);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        pressureResourceRelateTableMapper.deleteById(id);
    }

    /**
     * 分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateTableVO> pageList(PressureResourceRelateTableRequest request) {
        PressureResourceTableQueryParam param = new PressureResourceTableQueryParam();
        BeanUtils.copyProperties(request, param);
        if (request.getDsId() != null) {
            String dsKey = pressureResourceDsService.getDsKey(request.getDsId());
            param.setDsKey(dsKey);
        }
        PagingList<PressureResourceRelateTableEntity> pageList = pressureResourceRelateTableDAO.pageList(param);

        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelateTableEntity> source = pageList.getList();
        List<PressureResourceRelateTableVO> returnList = source.stream().map(configDto -> {
            PressureResourceRelateTableVO vo = new PressureResourceRelateTableVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, pageList.getTotal());
    }
}
