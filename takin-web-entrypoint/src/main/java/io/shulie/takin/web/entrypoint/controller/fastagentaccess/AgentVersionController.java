package io.shulie.takin.web.entrypoint.controller.fastagentaccess;

import java.io.FileNotFoundException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants.ModuleCode;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentVersionCreateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentVersionQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentUploadResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentVersionListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentUploadService;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentVersionService;
import io.shulie.takin.web.biz.utils.AppCommonUtil;
import io.shulie.takin.web.biz.utils.fastagentaccess.AgentDownloadUrlVerifyUtil;
import io.shulie.takin.web.biz.utils.fastagentaccess.ResponseFileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * agent版本管理(AgentVersion)controller
 *
 * @author ocean_wll
 * @date 2021-08-11 19:43:34
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL + "fast/agent/access")
@Api(tags = "接口：agent版本管理")
@Validated
@Slf4j
public class AgentVersionController {

    @Autowired
    private AgentUploadService agentUploadService;

    @Autowired
    private AgentVersionService agentVersionService;

    @ApiOperation("|_ 上传agent包")
    @PutMapping("/agent/upload")
    public AgentUploadResponse uploadFile(@NotNull(message = "文件不能为空") MultipartFile file) {
        return agentUploadService.upload(file);
    }

    @ApiOperation("|_ 发布新版本")
    @PostMapping("/release")
    @AuthVerification(
        moduleCode = ModuleCode.AGENT_VERSION,
        needAuth = ActionTypeEnum.CREATE
    )
    public void release(@Validated @RequestBody AgentVersionCreateRequest createRequest) {
        agentVersionService.release(createRequest);
    }

    //@ApiOperation("|_ 获取大版本列表")
    //@GetMapping("/firstVersionList")
    //public List<String> firstVersionList() {
    //    return agentVersionService.getFirstVersionList();
    //}

    @ApiOperation("|_ 列表分页查询")
    @GetMapping("/list")
    public PagingList<AgentVersionListResponse> agentList(AgentVersionQueryRequest queryRequest) {
        return agentVersionService.list(queryRequest);
    }

    @ApiOperation("|_ 获取所有agent版本列表")
    @GetMapping("/allVersionList")
    public List<String> allVersionList() {
        return agentVersionService.getAllVersionList();
    }

    @ApiOperation("|_ 原始探针包下载（页面）")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "version", value = "版本号", required = true),
    })
    @GetMapping("/download")
    @AuthVerification(
        moduleCode = ModuleCode.AGENT_VERSION,
        needAuth = ActionTypeEnum.DOWNLOAD
    )
    public void getFile(@RequestParam String version, HttpServletResponse response)
        throws FileNotFoundException {
        ResponseFileUtil.transfer(agentVersionService.getFile(version), false, null, false, response);
    }

    @ApiOperation("|_ 应用探针包下载（指令）")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectName", value = "应用名", required = true),
        @ApiImplicitParam(name = "userAppKey", value = "用户key", required = true),
        @ApiImplicitParam(name = "userId", value = "用户id", required = true),
        @ApiImplicitParam(name = "version", value = "agent版本号", required = true),
        @ApiImplicitParam(name = "envCode", value = "环境标识", required = true),
        @ApiImplicitParam(name = "expireDate", value = "过期时间", required = true),
        @ApiImplicitParam(name = "flag", value = "验证标识", required = true),
    })
    @GetMapping("/project/download")
    public void getProjectFile(@RequestParam String projectName, @RequestParam String userAppKey,
        @RequestParam String userId, @RequestParam String version, @RequestParam String envCode,
        @RequestParam Long expireDate, @RequestParam String flag, HttpServletResponse response) {
        if (!AgentDownloadUrlVerifyUtil.checkFlag(projectName, userAppKey, userId, version, envCode, expireDate,
            flag)) {
            throw AppCommonUtil.getCommonError("非法请求");
        }
        if (expireDate < System.currentTimeMillis()) {
            throw AppCommonUtil.getCommonError("链接已过期");
        }
        ResponseFileUtil.transfer(agentVersionService.getProjectFile(projectName, userAppKey, userId, version, envCode),
            true, null, false, response);
    }

    @ApiOperation("|_ 下载安装脚本")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectName", value = "应用名", required = true),
        @ApiImplicitParam(name = "version", value = "版本号", required = true),
        @ApiImplicitParam(name = "urlPrefix",
            value = "请求url的前缀，例如：http://tro-web.forcecop-dev-2.192.168.1.205.nip.io:31439", required = true),
    })
    @GetMapping("/installScript/download")
    @AuthVerification(
        moduleCode = ModuleCode.NEW_PROJECT_ACCESS,
        needAuth = ActionTypeEnum.CREATE
    )
    public void getScriptFile(@RequestParam String projectName, @RequestParam String version,
        @RequestParam String urlPrefix, HttpServletResponse response) {
        ResponseFileUtil.transfer(agentVersionService.getInstallScript(projectName, version, urlPrefix), true,
            "agentInstall.sh", true, response);
    }

    @ApiOperation("|_ 根据版本号查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "version", value = "版本号：不传查询最新版本，传查询指定版本")
    })
    @GetMapping("/queryLatestOrFixedVersion")
    public AgentVersionListResponse queryLatestOrFixedVersion(@RequestParam(required = false) String version) {
        return agentVersionService.queryLatestOrFixedVersion(version);
    }

}
