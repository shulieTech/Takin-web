package io.shulie.takin.adapter.cloud.impl.scene.manage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.cloud.ext.content.asset.AssetBillExt;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyOutputExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.response.Response;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.adapter.cloud.convert.SceneBusinessActivityRefInputConvert;
import io.shulie.takin.adapter.cloud.convert.SceneBusinessActivityRefRespConvertor;
import io.shulie.takin.adapter.cloud.convert.SceneManageRespConvertor;
import io.shulie.takin.adapter.cloud.convert.SceneScriptRefInputConvert;
import io.shulie.takin.adapter.cloud.convert.SceneScriptRefRespConvertor;
import io.shulie.takin.adapter.cloud.convert.SceneSlaRefInputConverter;
import io.shulie.takin.adapter.cloud.convert.SceneSlaRefRespConvertor;
import io.shulie.takin.adapter.cloud.convert.SceneTaskOpenConverter;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.adapter.api.model.request.scenemanage.CloudUpdateSceneFileRequest;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneIpNumReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageDeleteReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryByIdsReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptCheckAndUpdateReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageListResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.ScriptCheckResp;
import io.shulie.takin.adapter.api.model.response.strategy.StrategyResp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author 何仲奇
 * @date 2020/10/21 3:03 下午
 */
@Service
public class CloudSceneManageApiImpl implements CloudSceneManageApi {

    @Resource
    private CloudSceneManageService cloudSceneManageService;

    @Resource
    private CloudReportService cloudReportService;

    @Resource
    private PluginManager pluginManager;

    @Resource
    private StrategyConfigService strategyConfigService;

    @Override
    public void updateSceneFileByScriptId(CloudUpdateSceneFileRequest request) {
        if (Objects.isNull(request.getNewScriptId())
                || Objects.isNull(request.getOldScriptId())
                || Objects.isNull(request.getScriptType())
                || org.springframework.util.CollectionUtils.isEmpty(request.getUploadFiles())) {
            throw new IllegalArgumentException("缺少参数");
        }
        cloudSceneManageService.updateFileByScriptId(request);
    }

    @Override
    public Long saveScene(SceneManageWrapperReq req) {
        SceneManageWrapperInput input = new SceneManageWrapperInput();
        dataModelConvert(req, input);
        return cloudSceneManageService.addSceneManage(input);
    }

    @Override
    public String updateScene(SceneManageWrapperReq req) {
        SceneManageWrapperInput input = new SceneManageWrapperInput();
        dataModelConvert(req, input);
        cloudSceneManageService.updateSceneManage(input);
        return null;
    }

    @Override
    public String deleteScene(SceneManageDeleteReq req) {
        cloudSceneManageService.delete(req.getId());
        return "删除成功";
    }

    @Override
    public SceneManageWrapperResp getSceneDetail(SceneManageIdReq req) {
        return getDetailForEdit(req.getId(), req.getReportId());
    }

    @Override
    public SceneManageWrapperResp getSceneDetailNoAuth(SceneManageIdReq req) {
        return getDetailForEdit(req.getId(), req.getReportId());
    }

    @Override
    public List<SceneManageListResp> getSceneManageList(ContextExt req) {
        List<SceneManageListOutput> sceneManageListOutputs = cloudSceneManageService.querySceneManageList();
        return sceneManageListOutputs.stream()
                .map(t -> BeanUtil.copyProperties(t, SceneManageListResp.class))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> getSceneList(SceneManageQueryReq req) {
        SceneManageQueryInput queryVO = new SceneManageQueryInput();
        queryVO.setPageNumber(req.getPageNumber());
        queryVO.setPageSize(req.getPageSize());
        queryVO.setSceneId(req.getSceneId());
        queryVO.setSceneName(req.getSceneName());
        queryVO.setStatus(req.getStatus());
        List<Long> sceneIdList = new ArrayList<>();
        String sceneIds = req.getSceneIds();
        if (StringUtils.isNotBlank(sceneIds)) {
            String[] strList = sceneIds.split(",");
            for (String s : strList) {
                sceneIdList.add(Long.valueOf(s));
            }
        }
        queryVO.setSceneIds(sceneIdList);
        queryVO.setLastPtStartTime(req.getLastPtStartTime());
        queryVO.setLastPtEndTime(req.getLastPtEndTime());
        Integer isDeleted = req.getIsDeleted();
        queryVO.setIsDeleted(isDeleted == null ? 0 : isDeleted);
        PageInfo<SceneManageListOutput> pageInfo = cloudSceneManageService.queryPageList(queryVO);
        // 转换
        List<SceneManageListResp> list = pageInfo.getList().stream()
                .map(output -> {
                    SceneManageListResp resp = BeanUtil.copyProperties(output, SceneManageListResp.class);
                    resp.setHasAnalysisResult(StrUtil.isNotBlank(output.getScriptAnalysisResult()));
                    return resp;
                }).collect(Collectors.toList());
        return ResponseResult.success(list, pageInfo.getTotal());
    }

    @Override
    public BigDecimal calcFlow(SceneManageWrapperReq req) {
        BigDecimal flow = new BigDecimal(0);
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            AssetBillExt bill = BeanUtil.copyProperties(req, AssetBillExt.class);
            Response<BigDecimal> calcResponse = assetExtApi.calcEstimateAmount(bill);
            if (calcResponse.isSuccess()) {
                flow = calcResponse.getData();
            }
        }
        return flow;
    }

    @Override
    public StrategyResp getIpNum(SceneIpNumReq req) {
        StrategyOutputExt output = strategyConfigService.getStrategy(req.getConcurrenceNum(), req.getTpsNum());
        return BeanUtil.copyProperties(output, StrategyResp.class);
    }

    @Override
    public ScriptCheckResp checkAndUpdateScript(ScriptCheckAndUpdateReq request) {
        ScriptVerityRespExt scriptVerityRespExt = cloudSceneManageService.checkAndUpdate(
                request.getRequest(), request.getUploadPath(),
                request.isAbsolutePath(), request.isUpdate(), request.getVersion());
        return SceneTaskOpenConverter.INSTANCE.ofScriptVerityRespExt(scriptVerityRespExt);
    }

    @Override
    public List<SceneManageWrapperResp> queryByIds(SceneManageQueryByIdsReq req) {
        List<SceneManageWrapperOutput> outputs = cloudSceneManageService.getByIds(req.getSceneIds());
        return outputs.stream().map(output -> BeanUtil.copyProperties(output, SceneManageWrapperResp.class))
                .collect(Collectors.toList());
    }

    private SceneManageWrapperResp getDetailForEdit(Long id, Long reportId) {
        if (reportId != null && reportId != 0) {
            ReportResult reportBaseInfo = cloudReportService.getReportBaseInfo(reportId);
            if (reportBaseInfo != null) {
                id = reportBaseInfo.getSceneId();
            } else {
                throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在:" + reportId);
            }
        }
        SceneManageQueryOptions options = new SceneManageQueryOptions();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        options.setIncludeSLA(true);

        SceneManageWrapperOutput sceneManage = cloudSceneManageService.getSceneManage(id, options);
        assembleFeatures2(sceneManage);
        return wrapperSceneManage(sceneManage);
    }


    public void dataModelConvert(SceneManageWrapperReq wrapperReq, SceneManageWrapperInput input) {
        BeanUtil.copyProperties(wrapperReq, input);
        input.setStopCondition(SceneSlaRefInputConverter.ofList(wrapperReq.getStopCondition()));
        input.setWarningCondition(SceneSlaRefInputConverter.ofList(wrapperReq.getWarningCondition()));
        input.setBusinessActivityConfig(
                SceneBusinessActivityRefInputConvert.ofLists(wrapperReq.getBusinessActivityConfig()));
        input.setUploadFile(SceneScriptRefInputConvert.ofList(wrapperReq.getUploadFile()));
    }


    private void assembleFeatures2(SceneManageWrapperOutput resp) {
        String features = resp.getFeatures();
        if (StringUtils.isBlank(features)) {
            return;
        }
        Map<String, Object> map = JSON.parseObject(features, new TypeReference<Map<String, Object>>() {
        });
        Integer configType = -1;
        if (map.containsKey(SceneManageConstant.FEATURES_CONFIG_TYPE)) {
            configType = (Integer) map.get(SceneManageConstant.FEATURES_CONFIG_TYPE);
            resp.setConfigType(configType);
        }
        if (map.containsKey(SceneManageConstant.FEATURES_SCRIPT_ID)) {
            Long scriptId = Long.valueOf(map.get(SceneManageConstant.FEATURES_SCRIPT_ID).toString());
            if (configType == 1) {
                //业务活动
                List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> businessActivityConfig = resp
                        .getBusinessActivityConfig();
                for (SceneManageWrapperOutput.SceneBusinessActivityRefOutput data : businessActivityConfig) {
                    data.setScriptId(scriptId);
                    //业务活动的脚本id也在外面放一份
                    resp.setScriptId(scriptId);
                }
            } else {
                resp.setScriptId(scriptId);
            }
        }
        Object businessFlowId = map.get(SceneManageConstant.FEATURES_BUSINESS_FLOW_ID);
        resp.setBusinessFlowId(businessFlowId == null ? null : Long.valueOf(businessFlowId.toString()));

        // 新版本
        if (StrUtil.isNotBlank(resp.getScriptAnalysisResult())) {
            if (map.containsKey("dataValidation")) {
                JSONObject dataValidation = (JSONObject) map.get("dataValidation");
                Integer scheduleInterval = (Integer) dataValidation.get("timeInterval");
                resp.setScheduleInterval(scheduleInterval);
            }
        }
        //旧版本
        else {
            if (map.containsKey(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL)) {
                Integer scheduleInterval = (Integer) map.get(SceneManageConstant.FEATURES_SCHEDULE_INTERVAL);
                resp.setScheduleInterval(scheduleInterval);
            }
        }
    }

    private SceneManageWrapperResp wrapperSceneManage(SceneManageWrapperOutput sceneManage) {

        SceneManageWrapperResp response = SceneManageRespConvertor.INSTANCE.of(sceneManage);

        response.setBusinessActivityConfig(
                SceneBusinessActivityRefRespConvertor.INSTANCE.ofList(sceneManage.getBusinessActivityConfig()));
        response.setStopCondition(SceneSlaRefRespConvertor.INSTANCE.ofList(sceneManage.getStopCondition()));
        response.setWarningCondition(SceneSlaRefRespConvertor.INSTANCE.ofList(sceneManage.getWarningCondition()));
        response.setUploadFile(SceneScriptRefRespConvertor.INSTANCE.ofList(sceneManage.getUploadFile()));
        response.setScheduleInterval(sceneManage.getScheduleInterval());

        if (CollectionUtils.isEmpty(response.getBusinessActivityConfig())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "场景不存在或者没有业务活动配置");
        }
        sceneManage.getBusinessActivityConfig().forEach(data -> {
            if (StringUtils.isBlank(data.getBindRef())) {
                data.setBindRef("-1");
            }
            if (StringUtils.isBlank(data.getApplicationIds())) {
                data.setApplicationIds("-1");
            }
        });
        //旧版
        if (StringUtils.isBlank(sceneManage.getScriptAnalysisResult())) {
            Map<String, ThreadGroupConfigExt> map = sceneManage.getThreadGroupConfigMap();
            if (null != map) {
                ThreadGroupConfigExt tgConfig = map.get("all");
                if (null != tgConfig) {
                    response.setPressureMode(tgConfig.getMode());
                    if (null != tgConfig.getRampUp()) {
                        TimeBean time = new TimeBean(tgConfig.getRampUp().longValue(), tgConfig.getRampUpUnit());
                        response.setIncreasingSecond(time.getSecondTime());
                        response.setIncreasingTime(time);
                    }
                }
            }
        }
        return response;
    }

    @Override
    public String recovery(SceneManageDeleteReq req) {
        cloudSceneManageService.recovery(req.getId());
        return "恢复成功";
    }

    @Override
    public ResponseResult<List<SceneManageListResp>> querySceneByStatus(SceneManageQueryReq req) {
        List<SceneManageListOutput> sceneManageListOutputs = cloudSceneManageService.getSceneByStatus(req.getStatus());
        List<SceneManageListResp> list = sceneManageListOutputs.stream()
                .map(output -> {
                    SceneManageListResp resp = new SceneManageListResp();
                    BeanUtils.copyProperties(output, resp);
                    return resp;
                }).collect(Collectors.toList());
        return ResponseResult.success(list);
    }
}
