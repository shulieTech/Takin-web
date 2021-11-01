package io.shulie.takin.web.entrypoint.controller.confcenter;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * takin基础配置
 *
 * @author -
 */
@Api(tags = "takin基础配置")
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
public class BaseConfigController {

    private final Logger LOGGER = LoggerFactory.getLogger(BaseConfigController.class);

    @Autowired
    private BaseConfigService baseConfigService;

    /**
     * 新增配置
     *
     * @param tBaseConfig -
     * @return -
     */
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_GLOBAL_CONFIG_ADD,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "新增配置")
    public ResponseEntity<Object> addBaseConfig(@RequestBody TBaseConfig tBaseConfig) {
        try {
            baseConfigService.addBaseConfig(tBaseConfig);
            return ResponseOk.create("新增成功");
        } catch (Exception e) {
            LOGGER.error("BaseConfigController.updateBaseConfig 更新异常", e);
            return ResponseError.create(TakinErrorEnum.API_TAKIN_CONFCENTER_ADD_BASE_CONFIG_EXCEPTION.getErrorCode(),
                TakinErrorEnum.API_TAKIN_CONFCENTER_ADD_BASE_CONFIG_EXCEPTION.getErrorMessage());
        }
    }

    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_GLOBAL_CONFIG_QUERY,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取配置")
    public ResponseEntity<Object> queryByConfigCode(@RequestParam("configCode") String configCode) {
        if (StringUtils.isEmpty(String.valueOf(configCode))) {
            return ResponseError.create(TakinErrorEnum.API_TAKIN_CONFCENTER_BASE_CONFIG_QUERY_EXCEPTION.getErrorCode(), "参数缺失");
        }
        try {
            return ResponseOk.create(baseConfigService.queryByConfigCode(configCode));
        } catch (Exception e) {
            LOGGER.error("BaseConfigController.queryByConfigCode 查询异常", e);
            return ResponseError.create(TakinErrorEnum.API_TAKIN_CONFCENTER_BASE_CONFIG_QUERY_EXCEPTION.getErrorCode(),
                TakinErrorEnum.API_TAKIN_CONFCENTER_BASE_CONFIG_QUERY_EXCEPTION.getErrorMessage());
        }
    }

    @GetMapping(value = "/base/config", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取模板配置")
    public Response<String> queryConfigByCode(@RequestParam(value = "configCode") String configCode) {
        TBaseConfig config = baseConfigService.queryByConfigCode(configCode);
        return Response.success(config != null ? config.getConfigValue() : null);
    }

    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_GLOBAL_CONFIG_UPDATE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateBaseConfig(@RequestBody TBaseConfig tBaseConfig) {

        try {
            baseConfigService.updateBaseConfig(tBaseConfig);
            return ResponseOk.create("更新成功");
        } catch (Exception e) {
            LOGGER.error("BaseConfigController.updateBaseConfig 更新异常", e);
            return ResponseError.create(TakinErrorEnum.API_TAKIN_CONFCENTER_UPDATE_BASE_CONFIG_EXCEPTION.getErrorCode(),
                TakinErrorEnum.API_TAKIN_CONFCENTER_UPDATE_BASE_CONFIG_EXCEPTION.getErrorMessage());
        }
    }

}
