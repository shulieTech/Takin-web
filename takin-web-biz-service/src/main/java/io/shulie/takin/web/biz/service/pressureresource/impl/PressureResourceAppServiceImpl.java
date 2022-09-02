package io.shulie.takin.web.biz.service.pressureresource.impl;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceAppService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationAppVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationAppDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationAppMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationAppEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
@Service
public class PressureResourceAppServiceImpl implements PressureResourceAppService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceAppServiceImpl.class);

    @Resource
    private PressureResourceRelationAppDAO pressureResourceRelationAppDAO;

    @Resource
    private PressureResourceRelationAppMapper pressureResourceRelationAppMapper;

    /**
     * 应用检查列表
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationAppVO> appCheckList(PressureResourceAppRequest request) {
        PressureResourceAppQueryParam param = new PressureResourceAppQueryParam();
        BeanUtils.copyProperties(request, param);

        PagingList<PressureResourceRelationAppEntity> pageList = pressureResourceRelationAppDAO.pageList(param);
        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelationAppEntity> source = pageList.getList();
        List<PressureResourceRelationAppVO> returnList = source.stream().map(configDto -> {
            PressureResourceRelationAppVO vo = new PressureResourceRelationAppVO();
            BeanUtils.copyProperties(configDto, vo);
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, pageList.getTotal());
    }

    /**
     * 修改检查应用信息
     *
     * @param input
     */
    @Override
    public void update(PressureResourceAppInput input) {
        // 校验数据是否存在
        Long relationAppId = input.getId();
        if (relationAppId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        PressureResourceRelationAppEntity entity = pressureResourceRelationAppMapper.selectById(relationAppId);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }

        PressureResourceRelationAppEntity updateEntity = new PressureResourceRelationAppEntity();
        updateEntity.setId(input.getId());
        updateEntity.setIsolateType(input.getIsolateType());
        updateEntity.setJoinPressure(input.getJoinPressure());
        updateEntity.setGmtModified(new Date());
        pressureResourceRelationAppMapper.updateById(updateEntity);
    }
}
