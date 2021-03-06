package io.shulie.takin.web.biz.service.scene.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.SceneManageConstant;
import com.pamirs.takin.common.constant.TimeUnitEnum;
import com.pamirs.takin.common.exception.ApiException;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.common.util.ListHelper;
import com.pamirs.takin.common.util.parse.UrlUtil;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneScriptRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.entity.scenemanage.SceneBusinessActivityRef;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageWrapperVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneScriptRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.TimeVO;
import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.adapter.api.model.request.scenemanage.ReportActivityResp;
import io.shulie.takin.adapter.api.model.request.scenemanage.ReportDetailByIdsReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneIpNumReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneScriptRefOpen;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptCheckAndUpdateReq.EnginePlugin;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneStartCheckResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.adapter.api.model.response.strategy.StrategyResp;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneListForSelectOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneReportListOutput;
import io.shulie.takin.web.biz.pojo.request.filemanage.ScriptAndActivityVerifyRequest;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneForSelectRequest;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneReportRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.ScenePositionPointResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneSchedulerTaskResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneTagRefResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTagService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptTypeEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.common.InfluxDatabaseWriter;
import io.shulie.takin.web.data.dao.SceneExcludedApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigSceneRelateShipMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.param.CreateSceneExcludedApplicationParam;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/4/17 ??????3:32
 */
@Service
@Slf4j
public class SceneManageServiceImpl implements SceneManageService {
    @Resource
    private SceneTaskApi sceneTaskApi;
    @Resource
    private SceneService sceneService;
    @Resource
    private ApplicationDAO applicationDAO;
    @Resource
    private CloudSceneManageApi cloudSceneManageApi;
    @Resource
    private SceneManageApi sceneManageApi;
    @Resource
    private SceneTagService sceneTagService;
    @Resource
    private ScriptManageService scriptManageService;
    @Resource
    private BusinessLinkManageDAO businessLinkManageDAO;
    @Resource
    private SceneSchedulerTaskService sceneSchedulerTaskService;
    @Resource
    private ApplicationBusinessActivityService applicationBusinessActivityService;

    @Autowired
    private SceneExcludedApplicationDAO sceneExcludedApplicationDAO;

    @Autowired
    private InfluxDatabaseWriter influxDatabaseManager;

    @Autowired
    private SceneExcludedApplicationDAO excludedApplicationDAO;

    @Override
    public SceneDetailResponse getById(Long sceneId) {
        ResponseResult<SceneManageWrapperResp> sceneResult = this.detailScene(sceneId);
        if (sceneResult == null) {
            return null;
        }

        SceneDetailResponse sceneDetailResponse = DataTransformUtil.copyBeanPropertiesWithNull(sceneResult.getData(),
                SceneDetailResponse.class);
        if (sceneDetailResponse == null) {
            return null;
        }

        // ?????????????????????
        List<Long> excludedApplicationIds = excludedApplicationDAO.listApplicationIdsBySceneId(sceneId);
        sceneDetailResponse.setExcludedApplicationIds(DataTransformUtil.list2list(excludedApplicationIds, String.class));
        return sceneDetailResponse;
    }

    @Override
    public ResponseResult<List<SceneManageWrapperResp>> getByIds(SceneManageQueryByIdsReq req) {
        return sceneManageApi.getByIds(req);
    }

    @Override
    public WebResponse<List<SceneScriptRefOpen>> addScene(SceneManageWrapperVO vo) {
        /*
         * 0????????????????????????????????????????????????
         *    1??????????????????????????????
         *    2???Jmeter????????????????????????
         *    3??????????????????????????????????????????
         * 1?????????????????????+????????????
         * 2?????????????????????
         * 3???????????????
         * 4?????????SLA
         */
        if (!Objects.isNull(vo.getScheduleInterval())) {
            Long scheduleInterval = convertTime(Long.parseLong(String.valueOf(vo.getScheduleInterval())),
                    TimeUnitEnum.MINUTE.getValue());
            TimeVO timeVo = vo.getPressureTestTime();
            Long pressureTestSecond = convertTime(timeVo.getTime(), timeVo.getUnit());
            if (scheduleInterval > pressureTestSecond) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "????????????????????????????????????????????????");
            }
        }

        SceneManageWrapperReq req = new SceneManageWrapperReq();
        WebResponse<List<SceneScriptRefOpen>> webResponse = sceneManageVo2Req(vo, req);
        try {
            ResponseResult<Long> result = sceneManageApi.saveScene(req);
            if (vo.getIsScheduler() != null && vo.getIsScheduler() && result != null
                    && result.getSuccess() && vo.getExecuteTime() != null) {
                //????????????????????????
                Long sceneId = JSON.parseObject(JSON.toJSONString(result.getData()), Long.class);
                SceneSchedulerTaskCreateRequest createRequest = new SceneSchedulerTaskCreateRequest();
                createRequest.setSceneId(sceneId);
                createRequest.setExecuteTime(vo.getExecuteTime());
                // ????????????sql
                sceneSchedulerTaskService.insert(createRequest);
            }
            webResponse.setSuccess(true);

            // ?????????????????????
            if (result != null && result.getSuccess()) {
                this.createSceneExcludedApplication(result.getData(), vo.getExcludedApplicationIds());
            }

        } catch (Exception e) {
            log.error("????????????????????????", e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_ADD_ERROR, "???????????????????????????");
        }

        return webResponse;
    }

    /**
     * ???????????????
     *
     * @param dto ????????????????????????
     * @param req ????????????
     * @return ??????
     */
    private WebResponse<List<SceneScriptRefOpen>> sceneManageVo2Req(SceneManageWrapperVO dto, SceneManageWrapperReq req) {
        WebResponse<List<SceneScriptRefOpen>> webResponse = this.buildSceneManageRef(dto);
        List<SceneScriptRefOpen> data = webResponse.getData();
        if (!webResponse.getSuccess()) {
            return webResponse;
        }
        BeanUtils.copyProperties(dto, req);
        if (data != null) {
            req.setUploadFile(data);
        }
        //??????????????????
        if (dto.getPressureTestTime() != null) {
            TimeBean presTime = new TimeBean();
            presTime.setUnit(dto.getPressureTestTime().getUnit());
            presTime.setTime(dto.getPressureTestTime().getTime());
            req.setPressureTestTime(presTime);
        }
        //??????????????????
        if (dto.getIncreasingTime() != null) {
            TimeBean increasingTime = new TimeBean();
            increasingTime.setTime(dto.getIncreasingTime().getTime());
            increasingTime.setUnit(dto.getIncreasingTime().getUnit());
            req.setIncreasingTime(increasingTime);
            req.setIncreasingTime(increasingTime);
        }
        assembleFeatures(dto, req);
        return webResponse;
    }

    /**
     * ?????????????????????features
     *
     * @param vo  -
     * @param req -
     */
    public void assembleFeatures(SceneManageWrapperVO vo, SceneManageWrapperReq req) {
        Map<String, Object> map = new HashMap<>(5);
        map.put(SceneManageConstant.FEATURES_BUSINESS_FLOW_ID, vo.getBusinessFlowId());
        Integer configType = vo.getConfigType();
        map.put(SceneManageConstant.FEATURES_CONFIG_TYPE, configType);
        if (configType != null && configType == 1) {
            if (CollectionUtils.isNotEmpty(vo.getBusinessActivityConfig())) {
                map.put(SceneManageConstant.FEATURES_SCRIPT_ID, vo.getBusinessActivityConfig().get(0).getScriptId());
            }
        } else {
            map.put(SceneManageConstant.FEATURES_SCRIPT_ID, vo.getScriptId());
        }
        map.put(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL, vo.getScheduleInterval());
        req.setFeatures(JSON.toJSONString(map));
    }

    @Override
    public ResponseResult<List<SceneManageListOutput>> getPageList(SceneManageQueryVO vo) {

        SceneManageQueryReq req = BeanUtil.copyProperties(vo, SceneManageQueryReq.class);
        if (vo.getTagId() != null) {
            List<Long> tagIds = Collections.singletonList(vo.getTagId());
            List<SceneTagRefResponse> sceneTagRefBySceneIds = sceneTagService.getTagRefByTagIds(tagIds);
            if (CollectionUtils.isNotEmpty(sceneTagRefBySceneIds)) {
                List<Long> sceneIds = sceneTagRefBySceneIds.stream().map(SceneTagRefResponse::getSceneId).distinct().collect(Collectors.toList());
                String sceneIdStr = StringUtils.join(sceneIds, ",");
                req.setSceneIds(sceneIdStr);
            } else {
                return ResponseResult.success(new ArrayList<>(0), 0L);
            }
        }
        ResponseResult<List<SceneManageListResp>> sceneList = sceneManageApi.getSceneList(req);
        List<SceneManageListOutput> listData = convertData(sceneList.getData());
        List<Long> sceneIds = listData.stream().map(SceneManageListOutput::getId).collect(Collectors.toList());
        //?????????????????????????????????
        List<SceneSchedulerTaskResponse> responseList = sceneSchedulerTaskService.selectBySceneIds(sceneIds);
        Map<Long, Date> sceneExecuteTimeMap = new HashMap<>(responseList.size());
        responseList.forEach(response -> sceneExecuteTimeMap.put(response.getSceneId(), response.getExecuteTime()));
        listData.forEach(t -> {
            Date date = sceneExecuteTimeMap.get(t.getId());
            if (date != null) {
                t.setIsScheduler(true);
                t.setScheduleExecuteTime(DateUtil.formatDateTime(date));
            }

            //?????????????????????????????????
            QueryWrapper queryWrapper = new QueryWrapper();
            Long sceneId = t.getId();
            queryWrapper.eq("scene_id", sceneId);
            queryWrapper.eq("is_deleted", 0);
            InterfacePerformanceConfigSceneRelateShipEntity entity =
                    performanceConfigSceneRelateShipMapper.selectOne(queryWrapper);
            if (Objects.nonNull(entity)) {
                t.setConfigId(entity.getApiId());
            } else {
                // nothing
            }

        });
        return ResponseResult.success(listData, sceneList.getTotalNum());
    }

    @Resource
    InterfacePerformanceConfigSceneRelateShipMapper performanceConfigSceneRelateShipMapper;

    /**
     * ??????bean
     *
     * @return -
     */
    private List<SceneManageListOutput> convertData(List<SceneManageListResp> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            //????????????????????????
            List<TagManageResponse> allSceneTags = sceneTagService.getAllSceneTags();
            Map<Long, TagManageResponse> tagMap = new HashMap<>(allSceneTags.size());
            if (CollectionUtils.isNotEmpty(allSceneTags)) {
                allSceneTags.forEach(tagManageResponse -> tagMap.put(tagManageResponse.getId(), tagManageResponse));
            }
            List<Long> sceneIds = data.stream().map(SceneManageListResp::getId).collect(Collectors.toList());
            List<SceneTagRefResponse> sceneTagRefList = sceneTagService.getSceneTagRefBySceneIds(sceneIds);
            Map<Long, List<SceneTagRefResponse>> sceneMap = sceneTagRefList.stream().collect(
                    Collectors.groupingBy(SceneTagRefResponse::getSceneId));

            return data.stream().map(resp -> {
                SceneManageListOutput output = new SceneManageListOutput();
                BeanUtils.copyProperties(resp, output);
                // ????????????
                WebPluginUtils.fillQueryResponse(output);

                //????????????????????????
                List<TagManageResponse> tags = new ArrayList<>();
                if (sceneMap.containsKey(resp.getId())) {
                    List<SceneTagRefResponse> refTagList = sceneMap.get(resp.getId());
                    for (SceneTagRefResponse response : refTagList) {
                        Long tagId = response.getTagId();
                        if (tagMap.containsKey(tagId)) {
                            tags.add(tagMap.get(tagId));
                        }
                    }
                }
                output.setTag(tags);
                return output;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public ResponseResult<StrategyResp> getIpNum(Integer concurrenceNum, Integer tpsNum) {
        SceneIpNumReq req = new SceneIpNumReq();
        req.setConcurrenceNum(concurrenceNum);
        req.setTpsNum(tpsNum);
        return sceneManageApi.getIpNum(req);
    }

    /**
     * ????????????
     *
     * @param dto ?????????????????????
     * @return ??????
     */
    private WebResponse<List<SceneScriptRefOpen>> buildSceneManageRef(SceneManageWrapperVO dto) {
        // ????????????id???
        Long scriptId = 0L;
        boolean hasScriptId = false;
        if (dto.getConfigType() != null && dto.getConfigType() == 2) {
            scriptId = dto.getScriptId();
            hasScriptId = true;
        } else {
            // ??????????????????????????????????????????????????????
            List<SceneBusinessActivityRefVO> list = dto.getBusinessActivityConfig();
            if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
                scriptId = list.get(0).getScriptId();
                hasScriptId = true;
            }
        }
        if (!hasScriptId) {
            WebResponse.fail("scriptId can not be null !");
        }

        // ??????scriptId??????????????????
        ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(
                scriptId);
        if (scriptManageDeployDetail == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "?????????????????????!");
        }
        // ??????????????????, ???????????????????????????
        if (ScriptManageUtil.deployRefBusinessFlowType(scriptManageDeployDetail.getRefType())) {
            // ????????????id
            Long businessFlowId = Long.valueOf(scriptManageDeployDetail.getRefValue());
            SceneResult scene = sceneService.getScene(businessFlowId);
            if (null != scene) {
                dto.setScriptAnalysisResult(scene.getScriptJmxNode());
            }
        }

        // ??????????????????
        dto.setScriptType(scriptManageDeployDetail.getType());
        // ??????????????????
        List<SceneScriptRefOpen> scriptList = ScriptManageUtil.buildScriptRef(scriptManageDeployDetail);
        WebResponse<List<SceneScriptRefOpen>> response = this.checkScript(scriptManageDeployDetail.getType(), scriptList);
        if (!response.getSuccess()) {
            return response;
        }
        List<SceneScriptRefOpen> execList = response.getData();

        List<SceneBusinessActivityRef> businessActivityList =
                this.buildSceneBusinessActivityRef(dto.getBusinessActivityConfig());

        // ???????????????????????????????????????????????????????????????????????????????????????
        if (ScriptTypeEnum.JMETER.getCode().equals(dto.getScriptType())) {
            ScriptAndActivityVerifyRequest param = ScriptAndActivityVerifyRequest.builder()
                .isPressure(dto.isPressure()).sceneId(dto.getId()).sceneName(dto.getPressureTestSceneName())
                .scriptType(dto.getScriptType()).scriptList(execList)
                .absolutePath(true).businessActivityList(businessActivityList)
                .deployDetail(scriptManageDeployDetail).version(scriptManageDeployDetail.getScriptVersion()).build();
            ScriptCheckDTO checkDTO = this.checkScriptAndActivity(param);
            if (StringUtils.isNoneBlank(checkDTO.getErrmsg())) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, checkDTO.getErrmsg());
            }
        }

        //??????????????????
        Map<Long, SceneBusinessActivityRef> dataMap = ListHelper.transferToMap(businessActivityList,
                SceneBusinessActivityRef::getBusinessActivityId, data -> data);
        dto.getBusinessActivityConfig().forEach(vo -> {
            SceneBusinessActivityRef ref = dataMap.get(vo.getBusinessActivityId());
            if (ref != null) {
                vo.setBindRef(ref.getBindRef());
                vo.setApplicationIds(ref.getApplicationIds());
            }
        });
        WebResponse<List<SceneScriptRefOpen>> webResponse = new WebResponse<>();
        webResponse.setSuccess(true);
        webResponse.setData(scriptList);
        return webResponse;
    }

    @Override
    public WebResponse<String> updateScene(SceneManageWrapperVO dto) {
        if (Objects.nonNull(dto.getScheduleInterval())) {
            // ?????????????????????
            Long scheduleInterval = this.convertTime(dto.getScheduleInterval().longValue(),
                    TimeUnitEnum.MINUTE.getValue());
            TimeVO timeVo = dto.getPressureTestTime();
            Long pressureTestSecond = this.convertTime(timeVo.getTime(), timeVo.getUnit());
            if (scheduleInterval > pressureTestSecond) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "????????????????????????????????????????????????!");
            }
        }

        //??????????????????
        if (dto.getIsScheduler() == null || !dto.getIsScheduler()) {
            sceneSchedulerTaskService.deleteBySceneId(dto.getId());
        } else {
            SceneSchedulerTaskResponse dbData = sceneSchedulerTaskService.selectBySceneId(dto.getId());
            if (dto.getExecuteTime() != null && dbData != null) {
                Date executeTime = dbData.getExecuteTime();
                if (dto.getExecuteTime().compareTo(executeTime) != 0) {
                    SceneSchedulerTaskUpdateRequest updateParam = new SceneSchedulerTaskUpdateRequest();
                    updateParam.setId(dbData.getId());
                    updateParam.setExecuteTime(dto.getExecuteTime());
                    sceneSchedulerTaskService.update(updateParam, true);
                }
            } else if (dbData == null) {
                SceneSchedulerTaskCreateRequest createRequest = new SceneSchedulerTaskCreateRequest();
                createRequest.setSceneId(dto.getId());
                createRequest.setExecuteTime(dto.getExecuteTime());
                createRequest.setUserId(WebPluginUtils.traceUser().getId());
                sceneSchedulerTaskService.insert(createRequest);
            }
        }

        SceneManageWrapperReq req = new SceneManageWrapperReq();
        this.sceneManageVo2Req(dto, req);
        dto.setUploadFile(this.sceneScriptRefOpenToVoList(req.getUploadFile()));
        // ??????Cloud
        ResponseResult<String> cloudResult = sceneManageApi.updateScene(req);
        if (cloudResult.getError() != null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "takin-cloud???????????????" + cloudResult.getError().getMsg());
        }
        WebResponse<String> webResponse = new WebResponse<>();
        webResponse.setData(cloudResult.getData());
        webResponse.setSuccess(cloudResult.getSuccess());
        webResponse.setTotal(cloudResult.getTotalNum());

        // ?????????
        sceneExcludedApplicationDAO.removeBySceneId(dto.getId());
        // ??????????????????
        this.createSceneExcludedApplication(dto.getId(), dto.getExcludedApplicationIds());
        return webResponse;
    }

    public SceneScriptRefVO sceneScriptRefOpenToVo(SceneScriptRefOpen open) {
        SceneScriptRefVO vo = new SceneScriptRefVO();
        BeanUtils.copyProperties(open, vo);
        return vo;
    }

    public List<SceneScriptRefVO> sceneScriptRefOpenToVoList(List<SceneScriptRefOpen> source) {
        List<SceneScriptRefVO> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(source)) {
            source.forEach(open -> list.add(sceneScriptRefOpenToVo(open)));
        }
        return list;
    }

    @Override
    public String deleteScene(SceneManageDeleteReq vo) {
        return cloudSceneManageApi.deleteScene(vo);
    }

    @Override
    public ResponseResult<SceneManageWrapperResp> detailScene(Long id) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(id);
        // cloud ??????????????????
        ResponseResult<SceneManageWrapperResp> sceneDetail = sceneManageApi.getSceneDetail(req);

        if (Objects.isNull(sceneDetail) || Objects.isNull(sceneDetail.getData())) {
            if (sceneDetail.getError().getMsg().contains("19800-T0103")) {
                throw new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "??????????????????,??????????????????!");
            }
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "????????????????????????!");
        }
        if (!sceneDetail.getSuccess()) {
            ResponseResult.ErrorInfo error = sceneDetail.getError();
            String errorMsg = Objects.isNull(error) ? "" : error.getMsg();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "takin-cloud???????????????????????????????????????" + errorMsg);
        }

        SceneManageWrapperResp data = sceneDetail.getData();
        //????????????????????????
        SceneSchedulerTaskResponse sceneScheduler = sceneSchedulerTaskService.selectBySceneId(id);
        if (sceneScheduler != null && !sceneScheduler.getIsDeleted()) {
            Date executeTime = sceneScheduler.getExecuteTime();
            if (executeTime != null) {
                data.setIsScheduler(true);
                data.setExecuteTime(DateUtils.dateToString(executeTime, DateUtils.FORMATE_YMDHMS));
            }
        } else {
            data.setIsScheduler(false);
        }
        sceneDetail.setData(data);

        return sceneDetail;
    }

    @Override
    public ScriptCheckDTO checkBusinessActivityAndScript(SceneManageWrapperDTO sceneData) {
        int scriptType = sceneData.getScriptType();

        List<SceneScriptRefOpen> scriptList = Lists.newArrayList();
        sceneData.getUploadFile().forEach(data -> scriptList.add(buildSceneScriptRef(data)));

        ScriptCheckDTO dto = new ScriptCheckDTO();
        try {
            WebResponse<List<SceneScriptRefOpen>> response = checkScript(scriptType, scriptList);
            if (!response.getSuccess()) {
                dto.setErrmsg(response.getError().getMsg());
                return dto;
            }
            List<SceneScriptRefOpen> execList = response.getData();
            List<SceneBusinessActivityRef> businessActivityList = Lists.newArrayList();
            sceneData.getBusinessActivityConfig().forEach(
                data -> businessActivityList.add(buildSceneBusinessActivityRef(data)));
            if (ScriptTypeEnum.JMETER.getCode().equals(scriptType)) {
                ScriptAndActivityVerifyRequest param = ScriptAndActivityVerifyRequest.builder()
                    .isPressure(true).scriptType(scriptType).scriptList(execList)
                    .sceneId(sceneData.getId()).sceneName(sceneData.getPressureTestSceneName())
                    .absolutePath(sceneData.getIsAbsoluteScriptPath())
                    .businessActivityList(businessActivityList)
                    .deployDetail(scriptManageService.getScriptManageDeployDetail(sceneData.getScriptId())).build();
                dto = checkScriptAndActivity(param);
            }
        } catch (ApiException apiException) {
            dto.setMatchActivity(false);
            dto.setPtTag(false);
        }
        return dto;
    }

    private SceneBusinessActivityRef buildSceneBusinessActivityRef(SceneBusinessActivityRefDTO activityDTO) {
        SceneBusinessActivityRef activityRef = new SceneBusinessActivityRef();
        activityRef.setBusinessActivityId(activityDTO.getBusinessActivityId());
        activityRef.setBusinessActivityName(activityDTO.getBusinessActivityName());
        return activityRef;
    }

    private SceneScriptRefOpen buildSceneScriptRef(SceneScriptRefDTO scriptDTO) {
        SceneScriptRefOpen scriptRef = new SceneScriptRefOpen();
        scriptRef.setFileType(scriptDTO.getFileType());
        scriptRef.setIsDeleted(scriptDTO.getIsDeleted());
        scriptRef.setId(scriptDTO.getId());
        scriptRef.setUploadPath(scriptDTO.getUploadPath());
        return scriptRef;
    }

    /**
     * ??????????????????id???????????????id??????
     *
     * @param businessActivityId ????????????id
     * @return ??????id??????
     */
    @Override
    public List<String> getAppIdsByBusinessActivityId(Long businessActivityId) {
        return getAppIdsByNameAndUser(getAppsById(businessActivityId));
    }

    /**
     * ?????????????????? ??????
     *
     * @param voList ????????????
     * @return ??????????????????
     */
    private List<SceneBusinessActivityRef> buildSceneBusinessActivityRef(List<SceneBusinessActivityRefVO> voList) {
        List<SceneBusinessActivityRef> businessActivityList = Lists.newArrayList();
        voList.forEach(data -> {
            SceneBusinessActivityRef ref = new SceneBusinessActivityRef();
            ref.setBusinessActivityId(data.getBusinessActivityId());
            ref.setBusinessActivityName(data.getBusinessActivityName());
            List<String> ids = getAppIdsByNameAndUser(getAppsById(data.getBusinessActivityId()));
            ref.setApplicationIds(convertListToString(ids));
            ref.setGoalValue(buildGoalValue(data));
            businessActivityList.add(ref);
        });
        return businessActivityList;
    }

    private String buildGoalValue(SceneBusinessActivityRefVO vo) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(SceneManageConstant.TPS, vo.getTargetTPS());
        map.put(SceneManageConstant.RT, vo.getTargetRT());
        map.put(SceneManageConstant.SUCCESS_RATE, vo.getTargetSuccessRate());
        map.put(SceneManageConstant.SA, vo.getTargetSA());
        return JSON.toJSONString(map);
    }

    /**
     * ?????????????????????
     *
     * @param verifyParam           ????????????
     * @return dto
     */
    private ScriptCheckDTO checkScriptAndActivity(ScriptAndActivityVerifyRequest verifyParam) {
        Integer scriptType = verifyParam.getScriptType();
        ScriptCheckDTO dto = new ScriptCheckDTO();
        if (scriptType == null) {
            return new ScriptCheckDTO(false, false, "???????????????");
        }
        if (scriptType == 1) {
            return new ScriptCheckDTO();
        }

        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_SCRIPT_CHECK)) {
            return new ScriptCheckDTO();
        }

        List<SceneBusinessActivityRef> businessActivityList = verifyParam.getBusinessActivityList();

        ScriptCheckAndUpdateReq request = buildVerifyRequest(verifyParam);
        List<String> requestUrl = request.getRequest();

        List<Long> businessActivityIds = businessActivityList.stream().map(SceneBusinessActivityRef::getBusinessActivityId).distinct().collect(
                Collectors.toList());

        // ??????????????????????????????
        List<BusinessLinkResult> results = businessLinkManageDAO.getListByIds(businessActivityIds);
        Map<Long, List<BusinessLinkResult>> businessLinkMap = results.stream().collect(Collectors.groupingBy(BusinessLinkResult::getLinkId));

        businessActivityList.forEach(data -> {
            List<BusinessLinkResult> businessLinkResults = businessLinkMap.get(data.getBusinessActivityId());
            if (CollectionUtils.isEmpty(businessLinkResults)) {
                return;
            }
            BusinessLinkResult businessLinkResult = businessLinkResults.get(0);
            EntranceJoinEntity entranceJoinEntity;
            if (ActivityUtil.isNormalBusiness(businessLinkResult.getType())) {
                entranceJoinEntity = ActivityUtil.covertEntrance(businessLinkResult.getEntrace());
            } else {
                entranceJoinEntity = ActivityUtil.covertVirtualEntrance(businessLinkResult.getEntrace());
            }

            String convertUrl = UrlUtil.convertUrl(entranceJoinEntity);
            data.setBindRef(convertUrl);
            requestUrl.add(convertUrl);
        });
        ResponseResult<ScriptCheckResp> scriptCheckResp = sceneManageApi.checkAndUpdateScript(request);
        if (scriptCheckResp == null || !scriptCheckResp.getSuccess()) {
            log.error("cloud??????????????????????????????{}", request.getUploadPath());
            dto.setErrmsg(String.format("|cloud??????????????????%s???????????????,???????????????%s???", request.getUploadPath(), scriptCheckResp.getError().getMsg()));
            return dto;
        }

        if (!scriptCheckResp.getSuccess()) {
            dto.setErrmsg(String.format("cloud??????????????????%s???????????????,???????????????%s???", request.getUploadPath(), scriptCheckResp.getError().getMsg()));
            return dto;
        }

        if (scriptCheckResp.getData() != null && CollectionUtils.isNotEmpty(scriptCheckResp.getData().getErrorMsg())) {
            dto.setErrmsg(StringUtils.join(scriptCheckResp.getData().getErrorMsg(), "|"));
        }

        return dto;
    }

    /**
     * ????????????
     *
     * @return -
     */
    private WebResponse<List<SceneScriptRefOpen>> checkScript(Integer scriptType, List<SceneScriptRefOpen> scriptList) {
        if (CollectionUtils.isEmpty(scriptList)) {
            return WebResponse.fail("500", "?????????????????????");
        }

        // ??????????????????
        List<SceneScriptRefOpen> execList = scriptList.stream()
                .filter(data -> data.getFileType() != null && FileTypeEnum.SCRIPT.getCode().equals(data.getFileType())
                        && data.getIsDeleted() != null && data.getIsDeleted() == 0)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(execList)) {
            return WebResponse.fail("500", "?????????????????????");
        }

        if (0 == scriptType && execList.size() > 1) {
            return WebResponse.fail("500", "JMeter???????????????????????????????????????");
        }

        return WebResponse.success(execList);
    }

    private List<String> getAppIdsByNameAndUser(List<String> nameList) {
        if (CollectionUtils.isEmpty(nameList)) {
            return new ArrayList<>(0);
        }
        return applicationDAO.queryIdsByNameAndTenant(nameList, WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode());
    }

    private List<String> getAppsById(Long id) {
        return applicationBusinessActivityService.processAppNameByBusinessActiveId(id);
    }

    private String convertListToString(List<String> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String data : dataList) {
            sb.append(data);
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    /**
     * ???????????????????????????
     *
     * @param time ??????
     * @param unit ??????
     * @return ????????? ?????????
     */
    private Long convertTime(Long time, String unit) {
        if (time == null) {
            return 0L;
        }

        long t;
        if (TimeUnitEnum.HOUR.getValue().equals(unit)) {
            t = time * 60 * 60;
        } else if (TimeUnitEnum.MINUTE.getValue().equals(unit)) {
            t = time * 60;
        } else {
            t = time;
        }
        return t;
    }

    @Override
    public void checkParam(SceneManageWrapperVO sceneVO) {
        List<SceneBusinessActivityRefVO> activityRefVOList = sceneVO.getBusinessActivityConfig();
        if (CollectionUtils.isEmpty(activityRefVOList)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "??????????????????????????????");
        }
        for (SceneBusinessActivityRefVO sceneBusinessActivityRefVO : activityRefVOList) {
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetTPS())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "??????TPS????????????");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetRT())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "??????RT????????????");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetSuccessRate())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "???????????????????????????");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetSA())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "??????SA????????????");
            }
            if (sceneVO.getPressureType().equals(0) && sceneVO.getConcurrenceNum() < sceneVO.getIpNum()) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "???????????????????????????IP???");
            }
        }
    }

    @Override
    public WebResponse<List<SceneScriptRefOpen>> buildSceneForFlowVerify(SceneManageWrapperVO vo, SceneManageWrapperReq req, Long userId) {
        vo.setPressure(true);
        return sceneManageVo2Req(vo, req);
    }

    @Override
    public ResponseResult<List<ScenePositionPointResponse>> getPositionPoint(Long sceneId) {
        List<ScenePositionPointResponse> list = Lists.newArrayList();
        SceneStartPreCheckReq checkReq = new SceneStartPreCheckReq();
        checkReq.setSceneId(sceneId);
        ResponseResult<SceneStartCheckResp> result = sceneTaskApi.sceneStartPreCheck(checkReq);
        if (result != null) {
            if (!result.getSuccess()) {
                ResponseResult.ErrorInfo error = result.getError();
                if (error != null) {
                    throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, "cloud???????????????????????????" + error.getMsg());
                }
            }
            if (result.getSuccess()) {
                SceneStartCheckResp resultData = result.getData();
                if (resultData != null) {
                    //false = ??????csv?????????????????????0
                    //  Boolean hasUnread = resultData.getHasUnread();
                    List<SceneStartCheckResp.FileReadInfo> infos = resultData.getFileReadInfos();
                    if (Objects.nonNull(infos)) {
                        infos.forEach(t -> {
                            ScenePositionPointResponse response = new ScenePositionPointResponse();
                            response.setScriptName(t.getFileName());
                            response.setScriptSize(t.getFileSize());
                            response.setPressedSize(t.getReadSize());
                            list.add(response);
                        });
                    }
                    // redisTemplate.opsForValue().set("hasUnread_"+sceneId,hasUnread);
                }
            }
        }
        return ResponseResult.success(list);
    }

    /**
     * ??????????????????????????????
     *
     * @param excludedApplicationIds ???????????????ids
     * @param sceneId                ??????id
     */
    @Override
    public void createSceneExcludedApplication(Long sceneId, List<Long> excludedApplicationIds) {
        if (CollectionUtil.isEmpty(excludedApplicationIds)) {
            return;
        }

        List<CreateSceneExcludedApplicationParam> createSceneExcludedApplicationParams =
                excludedApplicationIds.stream().map(excludedApplicationId -> {
                    CreateSceneExcludedApplicationParam createSceneExcludedApplicationParam
                            = new CreateSceneExcludedApplicationParam();
                    createSceneExcludedApplicationParam.setSceneId(sceneId);
                    createSceneExcludedApplicationParam.setApplicationId(excludedApplicationId);
                    createSceneExcludedApplicationParam.setTenantId(WebPluginUtils.traceTenantId());
                    createSceneExcludedApplicationParam.setEnvCode(WebPluginUtils.traceEnvCode());
                    return createSceneExcludedApplicationParam;
                }).collect(Collectors.toList());

        // ?????????
        if (!sceneExcludedApplicationDAO.saveBatch(createSceneExcludedApplicationParams)) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "???????????????????????????!");
        }
    }


    @Override
    public String recoveryScene(SceneManageDeleteReq vo) {
        return cloudSceneManageApi.recovery(vo);
    }

    @Override
    public String archive(SceneManageDeleteReq vo) {
        return cloudSceneManageApi.archive(vo);
    }

    @Override
    public List<SceneListForSelectOutput> listForSelect(ListSceneForSelectRequest request) {
        // ??????cloud, ????????????, ??????????????????
        SceneManageQueryReq sceneManageQueryReq = new SceneManageQueryReq();
        sceneManageQueryReq.setStatus(request.getStatus());
        ResponseResult<List<SceneManageListResp>> cloudResult = sceneManageApi.querySceneByStatus(sceneManageQueryReq);
        if (cloudResult == null || !cloudResult.getSuccess() || CollectionUtil.isEmpty(cloudResult.getData())) {
            return Collections.emptyList();
        }

        return cloudResult.getData().stream().map(result -> {
            SceneListForSelectOutput sceneListForSelectOutput = new SceneListForSelectOutput();
            sceneListForSelectOutput.setId(result.getId());
            sceneListForSelectOutput.setName(result.getSceneName());
            return sceneListForSelectOutput;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SceneReportListOutput> listReportBySceneIds(ListSceneReportRequest request) {
        // ??????cloud, ????????????tps??????????????????
        List<ReportActivityResp> queryTpsParamList = this.listQueryTpsParam(request.getSceneIds());
        if (queryTpsParamList.isEmpty()) {
            return Collections.emptyList();
        }

        return queryTpsParamList.stream().map(queryTpsParam -> {
            // ????????????, ??????influxdb, ?????????tps, sql??????
            String influxDbSql = String.format("SELECT time as datetime, ((count - fail_count) / 5) AS tps "
                            + "FROM %s WHERE time <= NOW() AND time >= (NOW() - %dm) AND transaction = '%s' ORDER BY time",
                    this.getTableName(queryTpsParam), request.getInterval(),
                    queryTpsParam.getBusinessActivityList().get(0).getBindRef());

            List<SceneReportListOutput> reportList = influxDatabaseManager.query(influxDbSql, SceneReportListOutput.class, "jmeter");

            if (CollectionUtil.isEmpty(reportList)) {
                return null;
            }

            // ???????????????
            for (SceneReportListOutput report : reportList) {
                report.setSceneId(queryTpsParam.getSceneId());
                report.setSceneName(queryTpsParam.getSceneName());
                report.setTime(LocalDateTimeUtil.format(report.getDatetime(), "HH:mm:ss"));
            }

            return reportList;
        }).filter(Objects::nonNull).flatMap(Collection::stream)
                .sorted(Comparator.comparing(SceneReportListOutput::getTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneReportListOutput> rankReport(ListSceneReportRequest request) {
        // ??????cloud, ????????????tps??????????????????
        List<ReportActivityResp> queryTpsParamList = this.listQueryTpsParam(request.getSceneIds());
        if (queryTpsParamList.isEmpty()) {
            return Collections.emptyList();
        }

        return queryTpsParamList.stream().map(queryTpsParam -> {
            List<SceneReportListOutput> reportList = this.listReportFromInfluxDb(queryTpsParam);
            if (CollectionUtil.isEmpty(reportList) || reportList.get(0) == null) {
                return null;
            }

            SceneReportListOutput sceneReportListOutput = reportList.get(0);
            sceneReportListOutput.setSceneId(queryTpsParam.getSceneId());
            sceneReportListOutput.setSceneName(queryTpsParam.getSceneName());
            return sceneReportListOutput;
        }).filter(Objects::nonNull)
                .sorted(Comparator.comparing(SceneReportListOutput::getTps).reversed())
                .limit(10).collect(Collectors.toList());
    }

    /**
     * ????????????ids????????????tps?????????
     *
     * @param sceneIds ??????ids
     * @return ????????????
     */
    private List<ReportActivityResp> listQueryTpsParam(List<Long> sceneIds) {
        ReportDetailByIdsReq reportDetailByIdsReq = new ReportDetailByIdsReq();
        reportDetailByIdsReq.setSceneIds(sceneIds);
        ResponseResult<List<ReportActivityResp>> cloudResult = sceneTaskApi.listQueryTpsParam(reportDetailByIdsReq);
        if (cloudResult == null || !cloudResult.getSuccess() || CollectionUtil.isEmpty(cloudResult.getData())) {
            return Collections.emptyList();
        }

        return cloudResult.getData();
    }

    /**
     * ???????????????influxdb tableName
     *
     * @param queryTpsParam ??????
     * @return ??????
     */
    private String getTableName(ReportActivityResp queryTpsParam) {
        if (queryTpsParam.getJobId() != null) {
            return InfluxUtil.getMeasurement("pressure", queryTpsParam.getJobId());
        }
        return String.format("pressure_%d_%d_%d", queryTpsParam.getSceneId(), queryTpsParam.getReportId(),
                WebPluginUtils.getCustomerId());
    }

    /**
     * influxdb????????????tps
     *
     * @param queryTpsParam ????????????
     * @return ????????????
     */
    private List<SceneReportListOutput> listReportFromInfluxDb(ReportActivityResp queryTpsParam) {
        // ????????????, ??????influxdb, ?????????tps, sql??????
        String influxDbSql = String.format("SELECT time AS datetime, max(tps) AS tps FROM "
                        + "(SELECT ((count - fail_count) / 5) AS tps FROM %s WHERE transaction = '%s' ORDER BY time)",
                this.getTableName(queryTpsParam), queryTpsParam.getBusinessActivityList().get(0).getBindRef());
        List<SceneReportListOutput> reportList = influxDatabaseManager.query(influxDbSql, SceneReportListOutput.class, "jmeter");
        if (CollectionUtil.isEmpty(reportList)) {
            return Collections.emptyList();
        }

        return reportList;
    }

    private ScriptCheckAndUpdateReq buildVerifyRequest(ScriptAndActivityVerifyRequest verifyParam) {
        ScriptCheckAndUpdateReq checkReq = new ScriptCheckAndUpdateReq();
        checkReq.setPressure(verifyParam.isPressure());
        checkReq.setAbsolutePath(verifyParam.isAbsolutePath());
        checkReq.setUploadPath(verifyParam.getScriptList().get(0).getUploadPath());
        checkReq.setSceneId(verifyParam.getSceneId());
        checkReq.setSceneName(verifyParam.getSceneName());
        checkReq.setRequest(new ArrayList<>());
        parseDeployIfNecessary(verifyParam, checkReq);
        Integer version = verifyParam.getVersion();
        if (version != null) {checkReq.setVersion(version);}
        return checkReq;
    }

    private void parseDeployIfNecessary(ScriptAndActivityVerifyRequest verifyParam, ScriptCheckAndUpdateReq req) {
        if (req.isPressure()) {
            ScriptManageDeployDetailResponse deployDetail = verifyParam.getDeployDetail();
            if (Objects.nonNull(deployDetail)) {
                List<PluginConfigDetailResponse> pluginFiles = deployDetail.getPluginConfigDetailResponseList();
                if (CollectionUtils.isNotEmpty(pluginFiles)) {
                    List<EnginePlugin> refOutputList = pluginFiles.stream()
                        .map(plugin -> EnginePlugin.of(Long.valueOf(plugin.getId()), plugin.getVersion()))
                        .collect(Collectors.toList());
                    req.setPlugins(refOutputList);
                }
                if (Objects.isNull(verifyParam.getSceneId())) {
                    List<FileManageResponse> dataFiles = deployDetail.getFileManageResponseList();
                    if (CollectionUtils.isNotEmpty(dataFiles)) {
                        Map<Integer, List<FileManageResponse>> dataFilesMap = dataFiles.stream()
                            .collect(Collectors.groupingBy(FileManageResponse::getFileType, Collectors.toList()));
                        List<FileManageResponse> scriptFile = dataFilesMap.get(FileTypeEnum.SCRIPT.getCode());
                        if (CollectionUtils.isNotEmpty(scriptFile)) {
                            req.setScriptPath(scriptFile.get(0).getUploadPath());
                        }
                        List<FileManageResponse> csvFiles = dataFilesMap.get(FileTypeEnum.DATA.getCode());
                        if (CollectionUtils.isNotEmpty(csvFiles)) {
                            req.setCsvPaths(csvFiles.stream()
                                .map(FileManageResponse::getUploadPath).collect(Collectors.toList()));
                        }
                    }
                    List<FileManageResponse> attachmentFiles = deployDetail.getAttachmentManageResponseList();
                    if (CollectionUtils.isNotEmpty(attachmentFiles)) {
                        req.setAttachments(attachmentFiles.stream()
                            .map(FileManageResponse::getUploadPath).collect(Collectors.toList()));
                    }
                }
            }
        }
    }
}
