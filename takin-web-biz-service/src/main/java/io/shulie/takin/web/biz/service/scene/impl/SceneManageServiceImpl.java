package io.shulie.takin.web.biz.service.scene.impl;

import java.util.*;
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
import io.shulie.takin.adapter.api.model.request.scenemanage.*;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneStartCheckResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.adapter.api.model.response.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneListForSelectOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneReportListOutput;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneForSelectRequest;
import io.shulie.takin.web.biz.pojo.request.scene.ListSceneReportRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.ScenePositionPointResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneSchedulerTaskResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneTagRefResponse;
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
 * @date 2020/4/17 下午3:32
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

        // 查询排除的应用
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
         * 0、校验业务活动和脚本文件是否匹配
         *    1、是否有脚本脚本文件
         *    2、Jmeter脚本文件是否唯一
         *    3、业务活动必须存在于脚本文件
         * 1、保存基本信息+施压配置
         * 2、保存业务活动
         * 3、保存脚本
         * 4、保存SLA
         */
        if (!Objects.isNull(vo.getScheduleInterval())) {
            Long scheduleInterval = convertTime(Long.parseLong(String.valueOf(vo.getScheduleInterval())),
                TimeUnitEnum.MINUTE.getValue());
            TimeVO timeVo = vo.getPressureTestTime();
            Long pressureTestSecond = convertTime(timeVo.getTime(), timeVo.getUnit());
            if (scheduleInterval > pressureTestSecond) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "漏数验证时间间隔不能大于压测时长");
            }
        }

        SceneManageWrapperReq req = new SceneManageWrapperReq();
        WebResponse<List<SceneScriptRefOpen>> webResponse = sceneManageVo2Req(vo, req);
        try {
            ResponseResult<Long> result = sceneManageApi.saveScene(req);
            if (vo.getIsScheduler() != null && vo.getIsScheduler() && result != null
                && result.getSuccess() && vo.getExecuteTime() != null) {
                //保存场景定时信息
                Long sceneId = JSON.parseObject(JSON.toJSONString(result.getData()), Long.class);
                SceneSchedulerTaskCreateRequest createRequest = new SceneSchedulerTaskCreateRequest();
                createRequest.setSceneId(sceneId);
                createRequest.setExecuteTime(vo.getExecuteTime());
                // 插入拦截sql
                sceneSchedulerTaskService.insert(createRequest);
            }
            webResponse.setSuccess(true);

            // 忽略检测的应用
            if (result != null && result.getSuccess()) {
                this.createSceneExcludedApplication(result.getData(), vo.getExcludedApplicationIds());
            }

        } catch (Exception e) {
            log.error("新增压测场景失败", e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_ADD_ERROR, "新增压测场景失败！");
        }

        return webResponse;
    }

    /**
     * 场景转关联
     *
     * @param dto 更新所需要的参数
     * @param req 关联实例
     * @return 结果
     */
    private WebResponse<List<SceneScriptRefOpen>> sceneManageVo2Req(SceneManageWrapperVO dto, SceneManageWrapperReq req) {
        WebResponse<List<SceneScriptRefOpen>> webResponse = this.buildSceneManageRef(dto);
        List<SceneScriptRefOpen> data = webResponse.getData();
        if (!webResponse.getSuccess()) {return webResponse;}
        BeanUtils.copyProperties(dto, req);
        if (data != null) {
            req.setUploadFile(data);
        }
        //补充压测时间
        if (dto.getPressureTestTime() != null) {
            TimeBean presTime = new TimeBean();
            presTime.setUnit(dto.getPressureTestTime().getUnit());
            presTime.setTime(dto.getPressureTestTime().getTime());
            req.setPressureTestTime(presTime);
        }
        //补充递增时间
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
     * 将特殊字段放在features
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
        //计算场景的定时执行时间
        List<SceneSchedulerTaskResponse> responseList = sceneSchedulerTaskService.selectBySceneIds(sceneIds);
        Map<Long, Date> sceneExecuteTimeMap = new HashMap<>(responseList.size());
        responseList.forEach(response -> sceneExecuteTimeMap.put(response.getSceneId(), response.getExecuteTime()));
        listData.forEach(t -> {
            Date date = sceneExecuteTimeMap.get(t.getId());
            if (date != null) {
                t.setIsScheduler(true);
                t.setScheduleExecuteTime(DateUtil.formatDateTime(date));
            }

            //查询是否关联单接口压测
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
     * 转换bean
     *
     * @return -
     */
    private List<SceneManageListOutput> convertData(List<SceneManageListResp> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            //获取场景标签数组
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
                // 补充数据
                WebPluginUtils.fillQueryResponse(output);

                //组装标签关联关系
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
     * 场景关联
     *
     * @param dto 更新场景的入参
     * @return 结果
     */
    private WebResponse<List<SceneScriptRefOpen>> buildSceneManageRef(SceneManageWrapperVO dto) {
        // 文件脚本id，
        Long scriptId = 0L;
        boolean hasScriptId = false;
        if (dto.getConfigType() != null && dto.getConfigType() == 2) {
            scriptId = dto.getScriptId();
            hasScriptId = true;
        } else {
            // 业务活动类型的关联业务活动只能有一个
            List<SceneBusinessActivityRefVO> list = dto.getBusinessActivityConfig();
            if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
                scriptId = list.get(0).getScriptId();
                hasScriptId = true;
            }
        }
        if (!hasScriptId) {
            WebResponse.fail("scriptId can not be null !");
        }

        // 根据scriptId获取脚本信息
        ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(
            scriptId);
        if (scriptManageDeployDetail == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "脚本实例不存在!");
        }
        // 关联业务流程, 查出关联的业务流程
        if (ScriptManageUtil.deployRefBusinessFlowType(scriptManageDeployDetail.getRefType())) {
            // 业务流程id
            Long businessFlowId = Long.valueOf(scriptManageDeployDetail.getRefValue());
            SceneResult scene = sceneService.getScene(businessFlowId);
            if (null != scene) {
                dto.setScriptAnalysisResult(scene.getScriptJmxNode());
            }
        }

        // 设置脚本类型
        dto.setScriptType(scriptManageDeployDetail.getType());
        // 获得脚本列表
        List<SceneScriptRefOpen> scriptList = ScriptManageUtil.buildScriptRef(scriptManageDeployDetail);
        WebResponse<List<SceneScriptRefOpen>> response = this.checkScript(scriptManageDeployDetail.getType(), scriptList);
        if (!response.getSuccess()) {
            return response;
        }
        List<SceneScriptRefOpen> execList = response.getData();

        List<SceneBusinessActivityRef> businessActivityList =
            this.buildSceneBusinessActivityRef(dto.getBusinessActivityConfig());

        // 在上传文件时已经校验脚本和业务活动的关联关系，此处不再校验
        if (ScriptTypeEnum.JMETER.getCode().equals(dto.getScriptType())) {
            ScriptCheckDTO checkDTO = this.checkScriptAndActivity(dto.getScriptType(), true, businessActivityList,
                execList, scriptManageDeployDetail.getScriptVersion());
            if (StringUtils.isNoneBlank(checkDTO.getErrmsg())) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, checkDTO.getErrmsg());
            }
        }

        //填充绑定关系
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
            // 间隔转换为分钟
            Long scheduleInterval = this.convertTime(dto.getScheduleInterval().longValue(),
                TimeUnitEnum.MINUTE.getValue());
            TimeVO timeVo = dto.getPressureTestTime();
            Long pressureTestSecond = this.convertTime(timeVo.getTime(), timeVo.getUnit());
            if (scheduleInterval > pressureTestSecond) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "漏数验证时间间隔不能大于压测时长!");
            }
        }

        //处理定时任务
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
        // 调用Cloud
        ResponseResult<String> cloudResult = sceneManageApi.updateScene(req);
        if (cloudResult.getError() != null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "takin-cloud返回错误，" + cloudResult.getError().getMsg());
        }
        WebResponse<String> webResponse = new WebResponse<>();
        webResponse.setData(cloudResult.getData());
        webResponse.setSuccess(cloudResult.getSuccess());
        webResponse.setTotal(cloudResult.getTotalNum());

        // 先删除
        sceneExcludedApplicationDAO.removeBySceneId(dto.getId());
        // 排除应用更新
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
        // cloud 获得场景详情
        ResponseResult<SceneManageWrapperResp> sceneDetail = sceneManageApi.getSceneDetail(req);

        if (Objects.isNull(sceneDetail) || Objects.isNull(sceneDetail.getData())) {
            if(sceneDetail.getError().getMsg().contains("19800-T0103")){
                throw new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "数据签名异常,请联系管理员!");
            }
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "该压测场景不存在!");
        }
        if (!sceneDetail.getSuccess()) {
            ResponseResult.ErrorInfo error = sceneDetail.getError();
            String errorMsg = Objects.isNull(error) ? "" : error.getMsg();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, "takin-cloud返回的详情信息有误！原因：" + errorMsg);
        }

        SceneManageWrapperResp data = sceneDetail.getData();
        //组装场景定时时间
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
            if (0 == scriptType) {
                dto = checkScriptAndActivity(scriptType, sceneData.getIsAbsoluteScriptPath(),
                    businessActivityList, execList, null);
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
     * 通过业务活动id关联出应用id列表
     *
     * @param businessActivityId 业务活动id
     * @return 应用id列表
     */
    @Override
    public List<String> getAppIdsByBusinessActivityId(Long businessActivityId) {
        return getAppIdsByNameAndUser(getAppsById(businessActivityId));
    }

    /**
     * 业务活动列表 转换
     *
     * @param voList 业务活动
     * @return 业务活动列表
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
     * 检查脚本和活动
     *
     * @param scriptType           脚本类型
     * @param absolutePath         绝对路径
     * @param businessActivityList 业务活动列表
     * @param scriptList           脚本列表
     * @return dto
     */
    private ScriptCheckDTO checkScriptAndActivity(Integer scriptType, boolean absolutePath,
        List<SceneBusinessActivityRef> businessActivityList, List<SceneScriptRefOpen> scriptList, Integer version) {
        ScriptCheckDTO dto = new ScriptCheckDTO();
        if (scriptType == null) {
            return new ScriptCheckDTO(false, false, "无脚本文件");
        }
        if (scriptType == 1) {
            return new ScriptCheckDTO();
        }

        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_SCRIPT_CHECK)) {
            return new ScriptCheckDTO();
        }

        SceneScriptRefOpen sceneScriptRef = scriptList.get(0);

        ScriptCheckAndUpdateReq scriptCheckAndUpdateReq = new ScriptCheckAndUpdateReq();
        List<String> requestUrl = new ArrayList<>();
        scriptCheckAndUpdateReq.setAbsolutePath(absolutePath);
        scriptCheckAndUpdateReq.setRequest(requestUrl);
        scriptCheckAndUpdateReq.setUploadPath(sceneScriptRef.getUploadPath());
        if (version != null) {scriptCheckAndUpdateReq.setVersion(version);}

        List<Long> businessActivityIds = businessActivityList.stream().map(SceneBusinessActivityRef::getBusinessActivityId).distinct().collect(
            Collectors.toList());

        // 用户获取业务活动类型
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

        ResponseResult<ScriptCheckResp> scriptCheckResp = sceneManageApi.checkAndUpdateScript(scriptCheckAndUpdateReq);
        if (scriptCheckResp == null || !scriptCheckResp.getSuccess()) {
            log.error("cloud检查并更新脚本出错：{}", sceneScriptRef.getUploadPath());
            dto.setErrmsg(String.format("|cloud检查并更新【%s】脚本异常,异常内容【%s】", sceneScriptRef.getUploadPath(), scriptCheckResp.getError().getMsg()));
            return dto;
        }

        if (!scriptCheckResp.getSuccess()) {
            dto.setErrmsg(String.format("cloud检查并更新【%s】脚本异常,异常内容【%s】", sceneScriptRef.getUploadPath(), scriptCheckResp.getError().getMsg()));
            return dto;
        }

        if (scriptCheckResp.getData() != null && CollectionUtils.isNotEmpty(scriptCheckResp.getData().getErrorMsg())) {
            dto.setErrmsg(StringUtils.join(scriptCheckResp.getData().getErrorMsg(), "|"));
        }

        return dto;
    }

    /**
     * 校验脚本
     *
     * @return -
     */
    private WebResponse<List<SceneScriptRefOpen>> checkScript(Integer scriptType, List<SceneScriptRefOpen> scriptList) {
        if (CollectionUtils.isEmpty(scriptList)) {
            return WebResponse.fail("500", "找不到脚本文件");
        }

        // 可执行的脚本
        List<SceneScriptRefOpen> execList = scriptList.stream()
                .filter(data -> data.getFileType() != null && FileTypeEnum.SCRIPT.getCode().equals(data.getFileType())
                        && data.getIsDeleted() != null && data.getIsDeleted() == 0)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(execList)) {
            return WebResponse.fail("500", "找不到脚本文件");
        }

        if (0 == scriptType && execList.size() > 1) {
            return WebResponse.fail("500", "JMeter类型的脚本文件只允许有一个");
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
     * 秒转化为时、分、秒
     *
     * @param time 时间
     * @param unit 单位
     * @return 对应的 时分秒
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
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "业务活动配置不能为空");
        }
        for (SceneBusinessActivityRefVO sceneBusinessActivityRefVO : activityRefVOList) {
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetTPS())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标TPS不能为空");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetRT())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标RT不能为空");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetSuccessRate())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标成功率不能为空");
            }
            if (Objects.isNull(sceneBusinessActivityRefVO.getTargetSA())) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "目标SA不能为空");
            }
            if (sceneVO.getPressureType().equals(0) && sceneVO.getConcurrenceNum() < sceneVO.getIpNum()) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "最大并发数不能小于IP数");
            }
        }
    }

    @Override
    public WebResponse<List<SceneScriptRefOpen>> buildSceneForFlowVerify(SceneManageWrapperVO vo, SceneManageWrapperReq req, Long userId) {
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
                    throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, "cloud查询位点返回错误！" + error.getMsg());
                }
            }
            if (result.getSuccess()) {
                SceneStartCheckResp resultData = result.getData();
                if (resultData != null) {
                    //false = 没有csv文件或位点均为0
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
     * 创建场对应的排除应用
     *
     * @param excludedApplicationIds 排除的应用ids
     * @param sceneId 场景id
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

        // 再插入
        if (!sceneExcludedApplicationDAO.saveBatch(createSceneExcludedApplicationParams)) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "排除的应用保存失败!");
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
        // 查询cloud, 场景列表, 状态是压测中
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
        // 查询cloud, 获得查询tps所需要的条件
        List<ReportActivityResp> queryTpsParamList = this.listQueryTpsParam(request.getSceneIds());
        if (queryTpsParamList.isEmpty()) {
            return Collections.emptyList();
        }

        return queryTpsParamList.stream().map(queryTpsParam -> {
                    // 通过条件, 查询influxdb, 获取到tps, sql语句
                    String influxDbSql = String.format("SELECT time as datetime, ((count - fail_count) / 5) AS tps "
                                    + "FROM %s WHERE time <= NOW() AND time >= (NOW() - %dm) AND transaction = '%s' ORDER BY time",
                            this.getTableName(queryTpsParam), request.getInterval(),
                            queryTpsParam.getBusinessActivityList().get(0).getBindRef());

                    List<SceneReportListOutput> reportList = influxDatabaseManager.query(influxDbSql, SceneReportListOutput.class, "jmeter");

                    if (CollectionUtil.isEmpty(reportList)) {
                        return null;
                    }

                    // 时间戳处理
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
        // 查询cloud, 获得查询tps所需要的条件
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
     * 根据场景ids获取查询tps的参数
     *
     * @param sceneIds 场景ids
     * @return 查询参数
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
     * 压测报告的influxdb tableName
     *
     * @param queryTpsParam 参数
     * @return 表名
     */
    private String getTableName(ReportActivityResp queryTpsParam) {
        return String.format("pressure_%d_%d_%d", queryTpsParam.getSceneId(), queryTpsParam.getReportId(),
                WebPluginUtils.getCustomerId());
    }

    /**
     * influxdb查询报告tps
     *
     * @param queryTpsParam 请求参数
     * @return 报告列表
     */
    private List<SceneReportListOutput> listReportFromInfluxDb(ReportActivityResp queryTpsParam) {
        // 通过条件, 查询influxdb, 获取到tps, sql语句
        String influxDbSql = String.format("SELECT time AS datetime, max(tps) AS tps FROM "
                        + "(SELECT ((count - fail_count) / 5) AS tps FROM %s WHERE transaction = '%s' ORDER BY time)",
                this.getTableName(queryTpsParam), queryTpsParam.getBusinessActivityList().get(0).getBindRef());
        List<SceneReportListOutput> reportList = influxDatabaseManager.query(influxDbSql, SceneReportListOutput.class, "jmeter");
        if (CollectionUtil.isEmpty(reportList)) {
            return Collections.emptyList();
        }

        return reportList;
    }
}
