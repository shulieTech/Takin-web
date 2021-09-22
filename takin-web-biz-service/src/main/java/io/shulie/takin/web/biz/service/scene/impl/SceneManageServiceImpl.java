package io.shulie.takin.web.biz.service.scene.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.SceneManageConstant;
import com.pamirs.takin.common.constant.TimeUnitEnum;
import com.pamirs.takin.common.exception.ApiException;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.common.util.ListHelper;
import com.pamirs.takin.common.util.parse.UrlUtil;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.dao.linkmanage.TBusinessLinkManageTableMapper;
import com.pamirs.takin.entity.dao.linkmanage.TLinkManageTableMapper;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneScriptRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.entity.scenemanage.SceneBusinessActivityRef;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageIdVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageWrapperVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneScriptRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.TimeVO;
import io.shulie.takin.cloud.common.bean.TimeBean;
import io.shulie.takin.cloud.open.req.scenemanage.SceneIpNumReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageQueryReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneScriptRefOpen;
import io.shulie.takin.cloud.open.req.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageListResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.open.resp.scenemanage.ScriptCheckResp;
import io.shulie.takin.cloud.open.resp.strategy.StrategyResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.ScenePositionPointResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneSchedulerTaskResponse;
import io.shulie.takin.web.biz.pojo.response.scenemanage.SceneTagRefResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTagService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.common.constant.RemoteConstant;
import io.shulie.takin.web.common.domain.ErrorInfo;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptTypeEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.http.HttpAssert;
import io.shulie.takin.web.common.http.HttpWebClient;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/4/17 下午3:32
 */
@Service
@Slf4j
public class SceneManageServiceImpl implements SceneManageService {

    @Autowired
    private HttpWebClient httpWebClient;

    @Value("${script.check:true}")
    private Boolean scriptCheck;

    @Value("${script.check.perfomancetype:false}")
    private Boolean scriptPreTypeCheck;

    @Resource
    private TApplicationMntDao tApplicationMntDao;

    @Autowired
    private ApplicationBusinessActivityService applicationBusinessActivityService;

    @Autowired
    private ScriptManageService scriptManageService;

    @Autowired
    private SceneManageApi sceneManageApi;

    @Autowired
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @Autowired
    private SceneTagService sceneTagService;

    @Autowired
    private BusinessLinkManageDAO businessLinkManageDAO;

    @Autowired
    private SceneTaskApi sceneTaskApi;

    @Autowired
    private RedisTemplate redisTemplate;

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
        if (data != null) {req.setUploadFile(data);}
        //补充压测时间
        if (dto.getPressureTestTime() != null) {
            TimeBean presTime = new TimeBean();
            presTime.setUnit(dto.getPressureTestTime().getUnit());
            presTime.setTime(dto.getPressureTestTime().getTime());
            req.setPressureTestTime(presTime);
        }
        //补充递增时间
        if (dto.getIncreasingTime() != null) {
            TimeBean incresTime = new TimeBean();
            incresTime.setTime(dto.getIncreasingTime().getTime());
            incresTime.setUnit(dto.getIncreasingTime().getUnit());
            req.setIncreasingTime(incresTime);
            req.setIncreasingTime(incresTime);
        }
        assembleFeatures(dto, req);
        return webResponse;
    }

    //将特殊字段放在features
    public void assembleFeatures(SceneManageWrapperVO vo, SceneManageWrapperReq req) {
        Map<String, Object> map = new HashMap<>();
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
    public WebResponse getPageList(SceneManageQueryVO vo) {

        WebResponse webResponse = new WebResponse();
        webResponse.setData(Lists.newArrayList());
        webResponse.setSuccess(true);
        webResponse.setTotal(0L);
        SceneManageQueryReq req = new SceneManageQueryReq();
        BeanUtils.copyProperties(vo, req);
        if (vo.getTagId() != null) {
            List<Long> tagIds = Collections.singletonList(vo.getTagId());
            List<SceneTagRefResponse> sceneTagRefBySceneIds = sceneTagService.getTagRefByTagIds(tagIds);
            if (CollectionUtils.isNotEmpty(sceneTagRefBySceneIds)) {
                List<Long> sceneIds = sceneTagRefBySceneIds.stream().map(SceneTagRefResponse::getSceneId).distinct().collect(Collectors.toList());
                String sceneIdStr = StringUtils.join(sceneIds, ",");
                req.setSceneIds(sceneIdStr);
            } else {
                return webResponse;
            }
        }
        req.setLastPtStartTime(vo.getLastPtStartTime());
        req.setLastPtEndTime(vo.getLastPtEndTime());
        ResponseResult<List<SceneManageListResp>> sceneList = sceneManageApi.getSceneList(req);
        HttpAssert.isOk(sceneList, req, "cloud查询场景列表");
        List<SceneManageListOutput> listData = convertData(sceneList.getData());
        //计算场景的定时执行时间
        //List<SceneSchedulerTaskResponse> responseList = sceneSchedulerTaskService.selectBySceneIds(sceneIds);
        //Map<Long, String> sceneExcuteTimeMap = new HashMap<>();
        //responseList.stream().forEach(response -> {
        //    sceneExcuteTimeMap.put(response.getSceneId(), response.getExecuteTime());
        //});
        if (null != sceneList) {
            webResponse.setTotal(sceneList.getTotalNum());
        } else {
            webResponse.setTotal(0L);
        }
        webResponse.setSuccess(true);
        webResponse.setData(listData);
        return webResponse;
    }

    /**
     * 转换bean
     *
     * @return
     */
    private List<SceneManageListOutput> convertData(List<SceneManageListResp> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            //获取场景标签数组
            List<TagManageResponse> allSceneTags = sceneTagService.getAllSceneTags();
            Map<Long, TagManageResponse> tagMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(allSceneTags)) {
                allSceneTags.forEach(tagManageResponse -> {
                    tagMap.put(tagManageResponse.getId(), tagManageResponse);
                });
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

    //List<SceneListResponse> adjustSchedulerScene(List<SceneManageListResp> sceneRespList) {
    //
    //    if (CollectionUtils.isEmpty(sceneRespList)) {
    //        return Lists.newArrayList();
    //    }
    //    List<Long> sceneIds = sceneRespList.stream().map(SceneManageListResp::getId).collect(
    //            Collectors.toList());
    //    List<SceneSchedulerTaskResponse> responseList = sceneSchedulerTaskService.selectBySceneIds(sceneIds);
    //    Map<Long, Date> schedulerMap = new HashMap<>();
    //    responseList.stream().forEach(response -> {
    //        schedulerMap.put(response.getSceneId(), response.getExecuteTime());
    //    });
    //    return voList;
    //}

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
                execList);
            if (StringUtils.isNoneBlank(checkDTO.getErrmsg())) {
                return new WebResponse<List<SceneScriptRefOpen>>() {{
                    setError(ErrorInfo.build("500", checkDTO.getErrmsg()));
                }};
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
        WebResponse<List<SceneScriptRefOpen>> webResponse = new WebResponse<List<SceneScriptRefOpen>>();
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
    public WebResponse deleteScene(SceneManageIdVO vo) {
        vo.setRequestUrl(RemoteConstant.SCENE_MANAGE_URL);
        vo.setHttpMethod(HttpMethod.DELETE);
        return httpWebClient.request(vo);
    }

    @Override
    public ResponseResult<SceneManageWrapperResp> detailScene(Long id) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(id);
        // cloud 获得场景详情
        ResponseResult<SceneManageWrapperResp> sceneDetail = sceneManageApi.getSceneDetail(req);

        if (Objects.isNull(sceneDetail) || Objects.isNull(sceneDetail.getData())) {
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
        if (sceneScheduler != null && sceneScheduler.getIsDeleted() == false) {
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
                    businessActivityList, execList);
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
            List<String> ids = getAppIdsByNameAndUser(getAppsbyId(data.getBusinessActivityId()), null);
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
        List<SceneBusinessActivityRef> businessActivityList, List<SceneScriptRefOpen> scriptList) {
        ScriptCheckDTO dto = new ScriptCheckDTO();
        if (scriptType == null) {
            return new ScriptCheckDTO(false, false, "无脚本文件");
        }
        if (scriptType == 1) {
            return new ScriptCheckDTO();
        }
        if (scriptCheck == null || !scriptCheck) {
            return new ScriptCheckDTO();
        }
        SceneScriptRefOpen sceneScriptRef = scriptList.get(0);

        ScriptCheckAndUpdateReq scriptCheckAndUpdateReq = new ScriptCheckAndUpdateReq();
        List<String> requestUrl = new ArrayList<>();
        scriptCheckAndUpdateReq.setAbsolutePath(absolutePath);
        scriptCheckAndUpdateReq.setRequest(requestUrl);
        scriptCheckAndUpdateReq.setUploadPath(sceneScriptRef.getUploadPath());

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
            EntranceJoinEntity entranceJoinEntity = null;
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
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("cloud检查并更新【%s】脚本异常,异常内容【%s】",sceneScriptRef.getUploadPath(),scriptCheckResp.getError().getMsg()));
            dto.setErrmsg("|");
            return dto;
        }
        if (scriptCheckResp.getData() != null && CollectionUtils.isNotEmpty(scriptCheckResp.getData().getErrorMsg())) {
            StringBuilder stringBuilder = new StringBuilder();
            scriptCheckResp.getData().getErrorMsg().forEach(errorMsg -> {
                stringBuilder.append(errorMsg).append("|");
            });
            stringBuilder.substring(0, stringBuilder.length() - 1);
            dto.setErrmsg(stringBuilder.toString());
        }
        return dto;
    }

    /**
     * 校验脚本
     *
     * @return
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

    private List<String> getAppIdsByNameAndUser(List<String> nameList, Long userId) {
        if (CollectionUtils.isEmpty(nameList)) {
            return Collections.EMPTY_LIST;
        }
        return tApplicationMntDao.queryIdsByNameAndTenant(nameList, userId);
    }

    private List<String> getAppsbyId(Long id) {
        return applicationBusinessActivityService.processAppNameByBusinessActiveId(id);
    }

    private String convertListToString(List<String> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
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
        if (result != null) {V
            if (!result.getSuccess()) {
                ResponseResult.ErrorInfo error = Optional.ofNullable(result.getError()).orElse(null);
                if (error != null) {
                    throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, "cloud查询位点返回错误！" + error.getMsg());
                }
            }
            if (result.getSuccess()) {
                SceneStartCheckResp resultData = result.getData();
                if (resultData != null) {
                    ScenePositionPointResponse response = new ScenePositionPointResponse();
                    //false = 没有csv文件或位点均为0
//                    Boolean hasUnread = resultData.getHasUnread();
                    List<SceneStartCheckResp.FileReadInfo> infos = resultData.getFileReadInfos();
                    if (Objects.nonNull(infos)){
                        infos.stream().forEach(t -> {
                            response.setScriptName(t.getFileName());
                            response.setScriptSize(t.getFileSize());
                            response.setPressedSize(t.getReadSize());
                            list.add(response);
                        });
                    }
//                    redisTemplate.opsForValue().set("hasUnread_"+sceneId,hasUnread);
                }
            }
        }
        return ResponseResult.success(list);
    }
}
