package io.shulie.takin.web.biz.service.webide.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityInfoResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityNameResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListWebIDEResponse;
import io.shulie.takin.web.biz.service.linkmanage.LinkManageService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.webide.WebIDESyncService;
import io.shulie.takin.web.biz.service.webide.WebldeHelper;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMntMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: 南风
 * @Date: 2022/6/20 1:52 下午
 */
@Service
@Slf4j
public class WebIDESyncServiceImpl implements WebIDESyncService {

    @Resource
    private ThreadPoolExecutor webIDESyncThreadPool;

    @Resource
    private SceneService sceneService;

    @Resource
    private WebldeHelper webldeHelper;

    @Resource
    private LinkManageService linkManageService;


    @Resource
    private ApplicationMntMapper mntMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncScript(WebIDESyncScriptRequest request) {

        webIDESyncThreadPool.execute(() -> {
            webldeHelper.actuatorScene(request);
        });

    }


    @Override
    public PagingList<BusinessFlowListWebIDEResponse> sceneList(BusinessFlowPageQueryRequest queryRequest) {
        PagingList<BusinessFlowListResponse> list = sceneService.getBusinessFlowList(queryRequest);
        List<BusinessFlowListResponse> flowList = list.getList();
        if (flowList.isEmpty()) {
            return PagingList.of(new ArrayList<>(), list.getTotal());
        }
        List<BusinessFlowListWebIDEResponse> collect = flowList.stream().map(item -> {
            List<BusinessActivityNameResponse> activesByFlowId = linkManageService.getBusinessActiveByFlowId(item.getId());
            BusinessFlowListWebIDEResponse convert = Convert.convert(BusinessFlowListWebIDEResponse.class, item);
            convert.setVirtualNum(0);
            convert.setNormalNum(0);
            if (CollectionUtils.isEmpty(activesByFlowId)) {
                return convert;
            }
            Map<Integer, List<BusinessActivityNameResponse>> map = CollStreamUtil.groupByKey(activesByFlowId, BusinessActivityNameResponse::getType);
            if (map.containsKey(BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
                convert.setNormalNum(map.get(BusinessTypeEnum.NORMAL_BUSINESS.getType()).size());
            }
            if (map.containsKey(BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
                convert.setNormalNum(map.get(BusinessTypeEnum.VIRTUAL_BUSINESS.getType()).size());
            }

            return convert;
        }).collect(Collectors.toList());
        return PagingList.of(collect, list.getTotal());
    }

    @Override
    public List<BusinessActivityInfoResponse> activityList(Long businessFlowId) {
        List<BusinessActivityNameResponse> activesByFlowId = linkManageService.getBusinessActiveByFlowId(businessFlowId);
        if (CollectionUtils.isEmpty(activesByFlowId)) {
            return new ArrayList<>();
        }

        return activesByFlowId.stream().map(item -> {
            BusinessActivityInfoResponse convert = Convert.convert(BusinessActivityInfoResponse.class, item);
            convert.setActivityId(item.getBusinessActivityId());
            convert.setActivityName(item.getBusinessActivityName());
            if (Objects.nonNull(item.getApplicationId()) && StringUtils.isBlank(item.getApplicationName())) {
                convert.setApplicationName(mntMapper.selectApplicationName(String.valueOf(item.getApplicationId())));
            }
            if (StringUtils.isBlank(convert.getEntrace())) {
                return convert;
            }
            ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(convert.getEntrace());
            convert.setServiceName(entranceJoinEntity.getServiceName());
            convert.setMethodName(entranceJoinEntity.getMethodName());
            return convert;
        }).collect(Collectors.toList());
    }
}
