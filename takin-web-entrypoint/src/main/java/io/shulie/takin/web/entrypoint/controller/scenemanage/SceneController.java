package io.shulie.takin.web.entrypoint.controller.scenemanage;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.data.dao.filemanage.FileManageDAO;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptFileRefDAO;
import io.shulie.takin.cloud.open.request.scene.manage.SceneRequest;
import io.shulie.takin.cloud.open.req.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.open.api.scene.manage.MultipleSceneApi;
import io.shulie.takin.web.biz.pojo.request.scene.CreateSceneRequest;
import io.shulie.takin.web.biz.pojo.request.scene.UpdateSceneRequest;
import io.shulie.takin.web.data.result.scriptmanage.ScriptFileRefResult;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.cloud.open.response.scene.manage.SceneDetailResponse;

/**
 * 场景管理控制器 - 新
 *
 * @author 张天赐
 */
@RestController
@RequestMapping("/api/v2/scene")
public class SceneController {
    @Resource
    SceneService sceneService;
    @Resource
    FileManageDAO fileManageDao;
    @Resource
    MultipleSceneApi multipleSceneApi;
    @Resource
    ScriptFileRefDAO scriptFileRefDao;

    /**
     * 创建压测场景 - 新
     *
     * @return 创建的场景的自增ID
     */
    @PostMapping("create")
    @ApiOperation("创建压测场景 - 新")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_CREATE
    )
    @AuthVerification(needAuth = ActionTypeEnum.CREATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE)
    public ResponseResult<Long> create(@RequestBody @Valid CreateSceneRequest request) {
        SceneRequest sceneRequest = BeanUtil.copyProperties(request, SceneRequest.class);
        sceneRequest.setFile(assembleFileList(request.getBasicInfo().getScriptId()));
        return multipleSceneApi.create(sceneRequest);
    }

    /**
     * 更新压测场景 - 新
     *
     * @return 操作结果
     */
    @PostMapping("update")
    @ApiOperation("更新压测场景 - 新")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_UPDATE
    )
    @AuthVerification(needAuth = ActionTypeEnum.UPDATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE)
    public ResponseResult<Boolean> update(@RequestBody @Valid UpdateSceneRequest request) {
        SceneRequest sceneRequest = BeanUtil.copyProperties(request, SceneRequest.class);
        sceneRequest.setFile(assembleFileList(request.getBasicInfo().getScriptId()));
        return multipleSceneApi.update(sceneRequest);
    }

    /**
     * 获取压测场景详情 - 新
     *
     * @return 压测场景详情
     */
    @GetMapping("detail")
    @ApiOperation("获取压测场景详情 - 新")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.PRESSURE_TEST_MANAGE,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SCENE,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SCENE_UPDATE
    )
    @AuthVerification(needAuth = ActionTypeEnum.UPDATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE)
    public ResponseResult<SceneDetailResponse> detail(@RequestParam(required = false) Long sceneId) {
        return multipleSceneApi.detail(new SceneTaskStartReq() {{setSceneId(sceneId);}});
    }

    /**
     * 获取业务流程
     *
     * @return 业务流程列表
     */
    @GetMapping("business_activity_flow")
    @ApiOperation("获取业务流程列表 - 压测场景用")
    public ResponseResult<List<BusinessLinkManageTableEntity>> businessActivityFlow() {
        return ResponseResult.success(sceneService.businessActivityFlowList());
    }

    /**
     * 组装场景文件列表
     *
     * @param scriptId 脚本主键
     * @return 文件列表
     */
    private List<SceneRequest.File> assembleFileList(long scriptId) {
        // 根据脚本主键获取文件主键集合
        List<ScriptFileRefResult> scriptFileRefResults = scriptFileRefDao.selectFileIdsByScriptDeployId(scriptId);
        // 根据文件主键集合查询文件信息
        List<Long> fileIds = scriptFileRefResults.stream().map(ScriptFileRefResult::getFileId).collect(Collectors.toList());
        // 组装返回数据
        return fileManageDao.selectFileManageByIds(fileIds).stream().map(t -> {
            Map<String, Object> extend = JSONObject.parseObject(t.getFileExtend(), new TypeReference<Map<String, Object>>() {});
            return new SceneRequest.File() {{
                setName(t.getFileName());
                setPath(t.getUploadPath());
                setType(t.getFileType());
                setExtend(extend);
            }};
        }).collect(Collectors.toList());
    }
}
