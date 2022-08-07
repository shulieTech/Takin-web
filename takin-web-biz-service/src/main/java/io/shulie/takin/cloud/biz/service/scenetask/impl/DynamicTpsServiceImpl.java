package io.shulie.takin.cloud.biz.service.scenetask.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.StrUtil;
import io.shulie.takin.adapter.api.constant.ThreadGroupType;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi.JobConfig;
import io.shulie.takin.adapter.api.model.request.pressure.PressureParamModifyReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureParamsReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStartReq.ThreadConfigInfo;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskQueryTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskUpdateTpsInput;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.report.ReportOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneService;
import io.shulie.takin.cloud.biz.service.scenetask.DynamicTpsService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 动态TPS服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class DynamicTpsServiceImpl implements DynamicTpsService {
    @Resource
    private CloudSceneService cloudSceneService;
    @Resource
    private CloudSceneManageService cloudSceneManageService;
    @Resource
    private CloudReportService cloudReportService;
    @Resource
    private PressureTaskApi pressureTaskApi;

    /**
     * 获取动态TPS目标值
     *
     * @return 值-可能为空
     */
    @Override
    public Double get(SceneTaskQueryTpsInput input) {
        Long sceneId = input.getSceneId();
        ReportOutput report = cloudReportService.selectById(input.getReportId());
        PressureParamsReq req = new PressureParamsReq();
        req.setPressureId(report.getJobId());
        req.setRef(getThreadGroupMd5ByXpathMd5(sceneId, input.getXpathMd5()));
        try {
            List<JobConfig> params = pressureTaskApi.params(req);
            if (CollectionUtils.isEmpty(params)) {
                return null;
            }
            return params.stream().mapToDouble(param -> param.getContext().getTps()).sum();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过报告获取静态TPS目标
     *
     * @param reportId 报告主键
     * @param xpathMd5 脚本节点md5
     * @return TPS目标
     */
    @Override
    public double getStatic(long reportId, String xpathMd5) {
        // 获取报告
        ReportDetailOutput report = cloudReportService.getReportByReportId(reportId);
        // 转换节点MD5为线程组MD5
        String md5 = getThreadGroupMd5ByXpathMd5(report.getSceneId(), xpathMd5);
        // 获取场景
        SceneManageEntity scene = cloudSceneService.getScene(report.getSceneId());
        // 获取脚本解析结果
        String scriptAnalysisString = scene.getScriptAnalysisResult();
        List<ScriptNode> scriptAnalysis = JSON.parseArray(scriptAnalysisString, ScriptNode.class);
        //    脚本解析结果-一维展开
        List<ScriptNode> oneDepthScriptAnalysis = JmxUtil.toOneDepthList(scriptAnalysis);
        //    找到线程组节点
        if (oneDepthScriptAnalysis == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "脚本解析错误");
        }
        ScriptNode scriptNode = oneDepthScriptAnalysis.stream().filter(t -> md5.equals(t.getXpathMd5())).findAny()
            .orElse(null);
        if (scriptNode == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "未找到线程组节点");
        }
        //    获取线程组节点下所有
        if (scriptNode.getChildren() == null || scriptNode.getChildren().size() == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "线程组下没有其他节点");
        }
        //TODO 应该遍历出所有节点,然后找匹配的业务活动,然后算出总和的TPS
        throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "暂未实现");
    }

    /**
     * 设置动态TPS目标值
     *
     * @param input req
     */
    @Override
    public synchronized void set(SceneTaskUpdateTpsInput input) {
        Long sceneId = input.getSceneId();
        ReportOutput report = cloudReportService.selectById(input.getReportId());
        PressureParamModifyReq req = new PressureParamModifyReq();
        req.setPressureId(report.getJobId());
        String md5 = getThreadGroupMd5ByXpathMd5(sceneId, input.getXpathMd5());
        req.setRef(md5);
        SceneManageWrapperOutput sceneManage = cloudSceneManageService.getSceneManage(sceneId, null);
        ThreadGroupConfigExt configExt = sceneManage.getThreadGroupConfigMap().get(md5);
        req.setType(ThreadGroupType.of(configExt.getType(), configExt.getMode()));
        ThreadConfigInfo info = new ThreadConfigInfo();
        info.setTps(input.getTpsNum().intValue());
        req.setContext(info);
        pressureTaskApi.modifyParam(req);
    }

    /**
     * 根据节点MD5获取对应的线程组的MD5
     *
     * @param sceneId  场景主键
     * @param xpathMd5 节点MD5
     * @return 线程组MD5
     */
    private String getThreadGroupMd5ByXpathMd5(long sceneId, String xpathMd5) {
        // 获取场景
        SceneManageEntity scene = cloudSceneService.getScene(sceneId);
        // 获取脚本解析结果
        String scriptAnalysisString = scene.getScriptAnalysisResult();
        List<ScriptNode> scriptAnalysis = JSON.parseArray(scriptAnalysisString, ScriptNode.class);
        if (scriptAnalysis != null && scriptAnalysis.size() == 1) {
            HashMap<String, String> container = new HashMap<>(scriptAnalysis.size());
            scriptAnalysis.get(0).getChildren().forEach(
                t -> fullThreadGroupWhitNodeRelation(container, t.getXpathMd5(), t));
            String md5 = container.get(xpathMd5);
            if (StrUtil.isBlank(md5)) {
                throw new TakinCloudException(TakinCloudExceptionEnum.COMMON_VERIFY_ERROR, "未找到节点所对应的线程组节点");
            }
            return md5;
        }
        // 此处为旧版本数据
        return "all";
    }

    /**
     * 填充线程组和节点的关系
     *
     * @param container      容器
     *                       <ul>
     *                       <li>key:节点MD5</li>
     *                       <li>value:线程组MD5</li>
     *                       </ul>
     * @param threadGroupMd5 线程组MD5
     * @param node           节点
     */
    private void fullThreadGroupWhitNodeRelation(Map<String, String> container, String threadGroupMd5,
        ScriptNode node) {
        // 填充本节点
        container.put(node.getXpathMd5(), threadGroupMd5);
        // 填充子节点
        if (node.getChildren() != null) {
            node.getChildren().forEach(t -> fullThreadGroupWhitNodeRelation(container, threadGroupMd5, t));
        }
    }
}
