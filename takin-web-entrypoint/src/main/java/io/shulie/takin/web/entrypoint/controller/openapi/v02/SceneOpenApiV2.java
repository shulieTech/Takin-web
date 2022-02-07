package io.shulie.takin.web.entrypoint.controller.openapi.v02;

import javax.annotation.Resource;

import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.openapi.request.v02.SceneStartOpenApiReq;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author caijianying
 */
@Slf4j
@RestController
@RequestMapping(ApiUrls.TAKIN_OPEN_API_V02_URL + "scene/")
@Api(tags = "场景模块")
public class SceneOpenApiV2 {

    @Resource
    private SceneTaskService sceneTaskService;

    @Resource
    private SceneManageService sceneManageService;

    /**
     * 启动场景任务
     * @param req
     * @return
     */
    @PostMapping("startTask")
    public ResponseResult start(SceneStartOpenApiReq req) {
        if (req == null || req.getSceneId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "场景ID不能为空！");
        }
        ResponseResult resp = ResponseResult.success();
        try {
            //查询场景
            final ResponseResult<SceneManageWrapperResp> detailScene = sceneManageService.detailScene(req.getSceneId());
            final SceneManageWrapperResp sceneData = detailScene.getData();
            if (sceneData != null) {
                if (sceneData.getTenantId() == null){
                    throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "租户ID不能为空！");
                }
                TenantInfoExt infoExt = WebPluginUtils.getTenantInfo(sceneData.getTenantId());
                if (infoExt == null) {
                    log.error("租户信息未找到【{}】", sceneData.getTenantId());
                    throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "租户信息未找到,租户ID为【"+sceneData.getTenantId()+"】！");
                }
                TenantCommonExt ext = new TenantCommonExt();
                // 补充租户信息
                ext.setSource(ContextSourceEnum.JOB.getCode());
                ext.setTenantId(sceneData.getTenantId());
                ext.setEnvCode(sceneData.getEnvCode());
                ext.setTenantAppKey(ext.getTenantAppKey());
                WebPluginUtils.setTraceTenantContext(ext);
                //执行
                SceneActionParam startParam = new SceneActionParam();
                startParam.setSceneId(req.getSceneId());
                startParam.setEnvCode(sceneData.getEnvCode());
                startParam.setTenantId(sceneData.getTenantId());
                startParam.setUserId(sceneData.getUserId());
                startParam.setUserName(sceneData.getUserName());
                // 补充执行用户
                WebPluginUtils.setCloudUserData(new ContextExt() {{
                    setUserId(sceneData.getUserId());
                    setUserName(sceneData.getUserName());
                }});
                final SceneActionResp task = sceneTaskService.startTask(startParam);
                if (task != null){
                    resp.setData(task.getData());
                }
            }
        }catch (Exception e){
            log.error("openApi启动场景任务发生异常，场景ID={}",req.getSceneId(),e);
            resp = ResponseResult.fail(e.getMessage(),"请查看web端日志");
        }

        return resp;
    }

    /**
     * 获取任务状态
     * @param sceneId 场景id
     * @param reportId 报告id
     * @return {
     *     data: 2, //报告状态 0=待启动 1=启动中 2=压测中
     *     msg: []
     * }
     * @see io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp
     */
    @GetMapping("getTaskStatus")
    public ResponseResult getTaskStatus(Long sceneId, Long reportId){
        return sceneTaskService.checkStatus(sceneId,reportId);
    }
}
