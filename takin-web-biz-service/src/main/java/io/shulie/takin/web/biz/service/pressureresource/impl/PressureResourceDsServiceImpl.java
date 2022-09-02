package io.shulie.takin.web.biz.service.pressureresource.impl;

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.ExtInfo;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationDsInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationDsRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceDsService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceDsVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationAppVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationDsVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationAppDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationDsDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationDsMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationAppEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationDsEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/31 7:37 PM
 */
@Service
public class PressureResourceDsServiceImpl implements PressureResourceDsService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceDsServiceImpl.class);

    @Resource
    private PressureResourceRelationDsDAO pressureResourceRelationDsDAO;

    @Resource
    private PressureResourceRelationDsMapper pressureResourceRelationDsMapper;

    @Resource
    private PressureResourceRelationAppDAO pressureResourceRelationAppDAO;

    /**
     * 新增
     *
     * @param input
     */
    @Override
    public void add(PressureResourceRelationDsInput input) {
        // 批量给拆分到不同应用里面去，应用视角需要分页查询
        List<String> appNames = input.getRelationApps();
        if (CollectionUtils.isEmpty(appNames)) {
            return;
        }
        // 判断数据源是否已存在
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setBussinessDatabase(input.getBusinessDataBase());
        param.setResourceId(input.getResourceId());
        List<PressureResourceRelationDsEntity> list = pressureResourceRelationDsDAO.queryByParam(param);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "数据源已存在");
        }
        List<PressureResourceRelationDsEntity> dsEntitys = appNames.stream().map(appName -> {
            PressureResourceRelationDsEntity tmpEntity = new PressureResourceRelationDsEntity();

            BeanUtils.copyProperties(input, tmpEntity);
            tmpEntity.setAppName(appName);
            tmpEntity.setGmtCreate(new Date());

            ExtInfo extInfo = input.getExtInfo();
            tmpEntity.setExtInfo(JSON.toString(extInfo));
            return tmpEntity;
        }).collect(Collectors.toList());
        pressureResourceRelationDsDAO.add(dsEntitys);
    }

    /**
     * 数据源视图页面,内存分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationDsVO> listByDs(PressureResourceRelationDsRequest request) {
        // 查询所有的数据源信息
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setResourceId(request.getResourceId());
        param.setQueryBussinessDatabase(request.getQueryBusinessDataBase());

        List<PressureResourceRelationDsEntity> dsList = pressureResourceRelationDsDAO.queryByParam(param);
        // 相同数据源合并
        List<PressureResourceRelationDsVO> listVO = Lists.newArrayList();
        Map<String, List<PressureResourceRelationDsEntity>> dsMap = dsList.stream().collect(Collectors.groupingBy(ds -> ds.getBusinessDatabase()));
        for (Map.Entry<String, List<PressureResourceRelationDsEntity>> entry : dsMap.entrySet()) {
            List<PressureResourceRelationDsEntity> tmpList = entry.getValue();
            List<String> appNames = tmpList.stream().map(ds -> ds.getAppName()).collect(Collectors.toList());
            PressureResourceRelationDsVO tmpVO = new PressureResourceRelationDsVO();
            BeanUtils.copyProperties(tmpList.get(0), tmpVO);
            // 通过应用获取是否加入压测范围
            PressureResourceAppQueryParam appQueryParam = new PressureResourceAppQueryParam();
            appQueryParam.setAppNames(appNames);
            appQueryParam.setResourceId(request.getResourceId());
            List<PressureResourceRelationAppEntity> appEntitys = pressureResourceRelationAppDAO.queryList(appQueryParam);
            Map<String, List<PressureResourceRelationAppEntity>> appMap = appEntitys.stream().collect(Collectors.groupingBy(app -> app.getAppName()));
            List<PressureResourceRelationAppVO> appVOList = appNames.stream().map(app -> {
                PressureResourceRelationAppVO appVO = new PressureResourceRelationAppVO();
                appVO.setAppName(app);
                appVO.setJoinPressure(appMap.get(app).get(0).getJoinPressure());
                return appVO;
            }).collect(Collectors.toList());
            tmpVO.setAppList(appVOList);
            tmpVO.setSize(tmpList.size());
            listVO.add(tmpVO);
        }
        List<PressureResourceRelationDsVO> pageList = ListUtil.page(request.getCurrentPage() + 1, request.getPageSize(), listVO);
        return PagingList.of(pageList, listVO.size());
    }

    /**
     * 应用视图
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationDsVO> listByApp(PressureResourceRelationDsRequest request) {
        // 查询所有的数据源信息
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setResourceId(request.getResourceId());
        param.setQueryAppName(request.getQueryAppName());
        List<PressureResourceRelationDsEntity> dsList = pressureResourceRelationDsDAO.queryByParam(param);
        // 相同数据源合并
        List<PressureResourceRelationDsVO> listVO = Lists.newArrayList();
        Map<String, List<PressureResourceRelationDsEntity>> appMap = dsList.stream().collect(Collectors.groupingBy(ds -> ds.getAppName()));
        for (Map.Entry<String, List<PressureResourceRelationDsEntity>> entry : appMap.entrySet()) {
            List<PressureResourceRelationDsEntity> tmpList = entry.getValue();
            PressureResourceRelationDsVO tmpVO = new PressureResourceRelationDsVO();
            BeanUtils.copyProperties(tmpList.get(0), tmpVO);
            List<PressureResourceDsVO> dsVOList = tmpList.stream().map(ds -> {
                PressureResourceDsVO tmpDs = new PressureResourceDsVO();
                tmpDs.setBusinessDataBase(ds.getBusinessDatabase());
                tmpDs.setStatus(ds.getStatus());
                return tmpDs;
            }).collect(Collectors.toList());

            tmpVO.setDsList(dsVOList);
            tmpVO.setSize(tmpList.size());
            listVO.add(tmpVO);
        }
        List<PressureResourceRelationDsVO> pageList = ListUtil.page(request.getCurrentPage() + 1, request.getPageSize(), listVO);
        return PagingList.of(pageList, listVO.size());
    }
}
