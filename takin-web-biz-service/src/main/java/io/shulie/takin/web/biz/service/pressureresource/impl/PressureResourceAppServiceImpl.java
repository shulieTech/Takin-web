package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppRequest;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceAppService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationAppVO;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationAppDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationAppMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationAppEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import org.apache.commons.collections4.CollectionUtils;
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

    @Resource
    private ApplicationService applicationService;

    @Resource
    private PressureResourceMapper pressureResourceMapper;

    /**
     * 应用检查列表
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationAppVO> appCheckList(PressureResourceAppRequest request) {
        // 校验Resource
        PressureResourceEntity resourceEntity = pressureResourceMapper.selectById(request.getResourceId());
        if (resourceEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "配置未查询到");
        }
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
            // 设置主表隔离方式
            vo.setIsolateType(resourceEntity.getIsolateType());
            vo.setAgentNodeNum(0);
            vo.setStatus(1);
            vo.setId(String.valueOf(configDto.getId()));
            // 获取应用信息
            List<ApplicationListResponse> list = applicationService.getApplicationList(vo.getAppName());
            if (CollectionUtils.isNotEmpty(list)) {
                Response<ApplicationVo> voResponse = applicationService.getApplicationInfo(String.valueOf(list.get(0).getApplicationId()));
                if (voResponse.getSuccess()) {
                    ApplicationVo applicationVo = voResponse.getData();
                    vo.setAgentNodeNum(applicationVo.getOnlineNodeNum());
                    vo.setStatus(applicationVo.getAccessStatus().equals(0) ? 0 : 1);
                }
            }
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
