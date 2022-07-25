package io.shulie.takin.web.entrypoint.controller;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.service.sys.VersionService;
import io.shulie.takin.web.common.constant.ApiUrls;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "sys")
public class VersionController {

    @Resource
    private VersionService versionService;

    private static Map<String, Properties> VERSIONS = null;

    @GetMapping("version")
    public ResponseResult<Object> version() {
        return ResponseResult.success(versionService.selectVersion());
    }

    @PutMapping("version/confirm")
    public ResponseResult<Boolean> confirm() {
        versionService.confirm();
        return ResponseResult.success(true);
    }

    @GetMapping("codeVersion")
    public ResponseResult<Map<String, Properties>> gitVersions() {
        if (VERSIONS != null) {
            return ResponseResult.success(VERSIONS);
        }
        VERSIONS = versionService.gitVersions();
        return ResponseResult.success(VERSIONS);
    }
}