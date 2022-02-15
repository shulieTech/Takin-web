package io.shulie.takin.web.entrypoint.controller;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.biz.cache.DictionaryCache;
import io.shulie.takin.web.biz.pojo.request.config.UpdateConfigServerRequest;
import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.pojo.response.common.IsNewAgentResponse;
import io.shulie.takin.web.biz.service.ApiService;
import io.shulie.takin.web.biz.service.config.ConfigServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/5/14 下午7:34
 */
@RestController
@RequestMapping("/api/")
@Api(tags = "接口: 公共API")
public class ApiController {

    @Autowired
    private ApiService apiService;

    @Autowired
    private DictionaryCache dictionaryCache;

    @Autowired
    private ConfigServerService configServerService;

    @ApiOperation("|_ 数据字典")
    @GetMapping("link/dictionary")
    public Map<String, List<EnumResult>> dictionary(String key) {
        return dictionaryCache.getDicMap(key);
    }

    @ApiOperation("|_ 上传文件")
    @PostMapping("v2/file/upload")
    public FileUploadResponse uploadFile(@Validated FileUploadRequest request) {
        return apiService.uploadFile(request);
    }

    @ApiOperation("|_ 配置-应用是否是新版本agent")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "applicationId", value = "应用id", required = true,
            dataType = "long", paramType = "query")
    })
    @GetMapping("config/application/newAgent")
    public IsNewAgentResponse isNewAgent(@RequestParam Long applicationId) {
        return apiService.isNewAgentByApplication(applicationId);
    }

    @ApiOperation("|_ 修改服务配置")
    @PutMapping("configServer")
    public void updateConfigServer(@Validated @RequestBody UpdateConfigServerRequest updateConfigServerRequest) {
        configServerService.update(updateConfigServerRequest);
    }

}
