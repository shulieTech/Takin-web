package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceDetailInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceIsolateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceService;
import io.shulie.takin.web.biz.service.pressureresource.common.SourceTypeEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.StatusEnum;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceDetailVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceExtInfo;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceInfoVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceVO;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDetailDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateAppDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceDetailMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateDsMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateTableMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.*;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneUpdateParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDetailQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceQueryParam;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
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
    private PressureResourceMapper pressureResourceMapper;

    @Resource
    private PressureResourceRelateDsMapper pressureResourceRelateDsMapper;

    @Resource
    private PressureResourceRelateTableMapper pressureResourceRelateTableMapper;

    @Resource
    private PressureResourceDetailDAO pressureResourceDetailDAO;

    @Resource
    private PressureResourceDetailMapper pressureResourceDetailMapper;

    @Resource
    private PressureResourceRelateAppDAO pressureResourceRelateAppDAO;

    @Resource
    private PressureResourceRelateDsDAO pressureResourceRelateDsDAO;

    @Resource
    private SceneDAO sceneDAO;

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
        // 来源Id,业务流程Id
        insertEntity.setSourceId(input.getSourceId());
        insertEntity.setType(input.getType());
        insertEntity.setUserId(WebPluginUtils.traceUserId());
        insertEntity.setGmtCreate(new Date());
        insertEntity.setGmtModified(new Date());
        Long resourceId = pressureResourceDAO.add(insertEntity);

        // 获取详情
        List<PressureResourceDetailInput> detailInputs = input.getDetailInputs();
        if (CollectionUtils.isNotEmpty(detailInputs)) {
            List<PressureResourceDetailEntity> insertEntityList = convertEntitys(input.getType(), resourceId, detailInputs);
            pressureResourceDetailDAO.batchInsert(insertEntityList);
        }

        // 新增业务流程
        SceneCreateParam sceneCreateParam = new SceneCreateParam();
        sceneCreateParam.setSceneName(input.getName());
        sceneCreateParam.setType(1);
        sceneDAO.insert(sceneCreateParam);
    }

    @Override
    public void delete(Long resourceId) {
        if (resourceId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "参数未传递");
        }
        PressureResourceEntity resourceEntity = pressureResourceMapper.selectById(resourceId);
        if (resourceEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "数据不存在");
        }
        if (resourceEntity.getType().intValue() == SourceTypeEnum.AUTO.getCode()) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "此链路自动新增,无法删除");
        }
        // 删除主表
        pressureResourceMapper.deleteById(resourceId);
        // 删除详情
        QueryWrapper<PressureResourceDetailEntity> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("resource_id", resourceId);
        pressureResourceDetailMapper.delete(detailWrapper);

        // 删除数据源
        QueryWrapper<PressureResourceRelateDsEntity> dsWrapper = new QueryWrapper<>();
        dsWrapper.eq("resource_id", resourceId);
        pressureResourceRelateDsMapper.delete(dsWrapper);

        // 删除表
        QueryWrapper<PressureResourceRelateTableEntity> tableWrapper = new QueryWrapper<>();
        tableWrapper.eq("resource_id", resourceId);
        pressureResourceRelateTableMapper.delete(tableWrapper);
    }

    /**
     * 转换
     *
     * @param type
     * @param resourceId
     * @param detailInputs
     * @return
     */
    private List<PressureResourceDetailEntity> convertEntitys(int type, Long resourceId, List<PressureResourceDetailInput> detailInputs) {
        if (CollectionUtils.isEmpty(detailInputs)) {
            return Collections.EMPTY_LIST;
        }
        List<PressureResourceDetailEntity> insertEntityList = detailInputs.stream().map(detail -> {
            PressureResourceDetailEntity detailEntity = new PressureResourceDetailEntity();
            BeanUtils.copyProperties(detail, detailEntity);

            detailEntity.setId(null);
            // 来源类型
            detailEntity.setType(type);
            detailEntity.setResourceId(resourceId);
            String linkId = ActivityUtil.createLinkId(detail.getEntranceUrl(), detail.getMethod(),
                    detail.getAppName(), detail.getRpcType(), detail.getExtend());
            detailEntity.setLinkId(linkId);
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
        updateResourceEntity.setUserId(input.getUserId());
        pressureResourceMapper.updateById(updateResourceEntity);

        // 新增业务流程
        // 按名字查询链路
        SceneQueryParam sceneQueryParam = new SceneQueryParam();
        sceneQueryParam.setSceneName(input.getName());
        List<SceneResult> list = sceneDAO.selectListByName(sceneQueryParam);
        if (CollectionUtils.isEmpty(list)) {
            SceneCreateParam sceneCreateParam = new SceneCreateParam();
            sceneCreateParam.setSceneName(input.getName());
            sceneCreateParam.setUserId(WebPluginUtils.traceUserId());
            sceneCreateParam.setType(1);
            sceneDAO.insert(sceneCreateParam);
        } else {
            SceneUpdateParam updateParam = new SceneUpdateParam();
            updateParam.setSceneName(input.getName());
            updateParam.setUpdateTime(new Date());
            updateParam.setId(list.get(0).getId());
            updateParam.setUserId(WebPluginUtils.traceUserId());
            sceneDAO.update(updateParam);
        }

        // 修改详情
        PressureResourceDetailQueryParam detailParam = new PressureResourceDetailQueryParam();
        detailParam.setResourceId(input.getId());
        List<PressureResourceDetailEntity> oldList = pressureResourceDetailDAO.getList(detailParam);
        List<PressureResourceDetailEntity> newList = convertEntitys(input.getType(), input.getId(), input.getDetailInputs());

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
        // 自动梳理出来的不做删除操作
        if (input.getType().intValue() != SourceTypeEnum.AUTO.getCode()) {
            // 删除的,不在newMap里面的,
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
    }

    private String fetchKey(PressureResourceDetailEntity ele) {
        return String.format("%s|%s|%s|%s|%s|%s", ele.getEntranceUrl(), ele.getMethod(), ele.getAppName(), ele.getRpcType(), ele.getExtend());
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
            tmpDetailVo.setId(String.valueOf(detail.getId()));
            tmpDetailVo.setResourceId(String.valueOf(detail.getResourceId()));
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
        pressureResourceMapper.updateById(updateEntity);
    }

    /**
     * 处理进度
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Integer> progress(Long id) {
        Map<String, Integer> statusMap = Maps.newHashMap();
        statusMap.put("APP", 0);
        statusMap.put("DS", 0);
        statusMap.put("REMOTECALL", 0);

        // 查看应用状态
        PressureResourceAppQueryParam appQueryParam = new PressureResourceAppQueryParam();
        appQueryParam.setResourceId(id);
        List<PressureResourceRelateAppEntity> appEntityList = pressureResourceRelateAppDAO.queryList(appQueryParam);
        if (CollectionUtils.isNotEmpty(appEntityList)) {
            // 判断状态是否都是正常的
            int normal = appEntityList.stream().filter(app -> app.getStatus().intValue() == 0).collect(Collectors.toList()).size();
            if (normal == appEntityList.size()) {
                statusMap.put("APP", 2);
            }
            // 存在正常的,进行中
            if (appEntityList.size() - normal > 0) {
                statusMap.put("APP", 1);
            }
        }
        // 影子资源检查
        PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        dsQueryParam.setResourceId(id);
        List<PressureResourceRelateDsEntity> dsEntityList = pressureResourceRelateDsDAO.queryByParam(dsQueryParam);
        if (CollectionUtils.isNotEmpty(dsEntityList)) {
            // 判断状态是否都是正常的
            int normal = dsEntityList.stream().filter(ds -> ds.getStatus().intValue() == 2).collect(Collectors.toList()).size();
            if (normal == dsEntityList.size()) {
                statusMap.put("DS", 2);
            }
            // 存在正常的,进行中
            if (dsEntityList.size() - normal > 0) {
                statusMap.put("DS", 1);
            }
        }
        return statusMap;
    }

    /**
     * 处理页面汇总数据
     *
     * @return
     */
    @Override
    public PressureResourceExtInfo appInfo(Long id) {
        PressureResourceExtInfo extInfo = new PressureResourceExtInfo();
        extInfo.setTotalSize(0);
        extInfo.setExceptionSize(0);
        extInfo.setNormalSize(0);

        PressureResourceEntity entity = pressureResourceMapper.selectById(id);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "配置不存在");
        }
        PressureResourceAppQueryParam queryParam = new PressureResourceAppQueryParam();
        queryParam.setResourceId(id);
        List<PressureResourceRelateAppEntity> appEntityList = pressureResourceRelateAppDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(appEntityList)) {
            // 总的应用数
            extInfo.setTotalSize(appEntityList.size());
            // 正常的应用数
            Long normalSize = appEntityList.stream().filter(app -> app.getStatus().equals(0)).count();
            extInfo.setNormalSize(normalSize.intValue());
            extInfo.setExceptionSize(appEntityList.size() - normalSize.intValue());
        }
        // 检测时间都是一批的
        extInfo.setCheckTime(entity.getCheckTime());
        extInfo.setUserName(WebPluginUtils.getUserName(entity.getUserId(), WebPluginUtils.getUserMapByIds(Arrays.asList(entity.getUserId()))));
        return extInfo;
    }

    /**
     * 汇总信息-数据源
     *
     * @param id
     * @return
     */
    @Override
    public PressureResourceExtInfo dsInfo(Long id) {
        PressureResourceExtInfo extInfo = new PressureResourceExtInfo();
        extInfo.setTotalSize(0);
        extInfo.setExceptionSize(0);
        extInfo.setNormalSize(0);

        PressureResourceEntity entity = pressureResourceMapper.selectById(id);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "配置不存在");
        }
        PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        dsQueryParam.setResourceId(id);
        List<PressureResourceRelateDsEntity> dsEntityList = pressureResourceRelateDsDAO.queryByParam(dsQueryParam);

        if (CollectionUtils.isNotEmpty(dsEntityList)) {
            // 分组一下
            Map<String, List<PressureResourceRelateDsEntity>> dsMap = dsEntityList.stream().collect(Collectors.groupingBy(ds -> ds.getUniqueKey()));
            extInfo.setTotalSize(dsMap.size());
            int normalSize = 0;
            for (Map.Entry<String, List<PressureResourceRelateDsEntity>> entry : dsMap.entrySet()) {
                PressureResourceRelateDsEntity dsEntity = entry.getValue().get(0);
                if (dsEntity.getStatus().intValue() == StatusEnum.SUCCESS.getCode()) {
                    normalSize = normalSize + 1;
                }
            }
            extInfo.setNormalSize(normalSize);
            extInfo.setExceptionSize(extInfo.getTotalSize() - extInfo.getNormalSize());
        }
        // 检测时间都是一批的
        extInfo.setCheckTime(entity.getCheckTime());
        extInfo.setUserName(WebPluginUtils.getUserName(entity.getUserId(), WebPluginUtils.getUserMapByIds(Arrays.asList(entity.getUserId()))));
        return extInfo;
    }
}
