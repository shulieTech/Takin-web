package io.shulie.takin.web.entrypoint.controller.confcenter;

import java.util.Map;

import javax.validation.Valid;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.domain.vo.TShadowTableConfigVo;
import com.pamirs.takin.entity.domain.vo.TShadowTableDatasourceVo;
import io.shulie.takin.web.biz.service.ShadowTableConfigService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 298403
 * 影子表配置 controller
 */
@Api(tags = "影子表配置")
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL)
public class ShadowTableConfigController {

    private final Logger LOGGER = LoggerFactory.getLogger(ShadowTableConfigController.class);

    @Autowired
    private ShadowTableConfigService shadowTableConfigService;

    /**
     * agent通过 appName 获取 影子表配置 并且更新agent版本
     *
     * @param appName appName
     *
     * @return 成功, 则返回影子表配置信息, 失败则返回错误编码和错误信息
     */
    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_AGENT_GET_SHADOWCONFIG_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> agentGetShadowTableConfig(@RequestParam("appName") String appName) {
        try {
            return ResponseOk.create(shadowTableConfigService.agentGetShadowTable(appName));
        } catch (Exception e) {
            LOGGER.error(
                    "ShadowTableConfigController.pradarGetShadowTableConfig 查询appName下影子表配置异常 appName {}, Exception {}",
                    appName, ExceptionUtils.getFullStackTrace(e));
            return ResponseError.create(
                    TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_PRADAR_GET_EXCEPTION.getErrorCode(),
                    TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_PRADAR_GET_EXCEPTION.getErrorMessage());
        }
    }


    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_GET_CONFIG_PAGE_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryShadowTableConfigPage(@RequestBody Map<String, Object> paramMap) {
        try {
            return ResponseOk.create(shadowTableConfigService.queryShadowTableConfigPage(paramMap));
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.queryShadowTableConfigPage 获取影子表配置分页异常 {}", e);
            return ResponseError.create(1010100102, "获取影子表配置分页异常");
        }
    }

    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_UPDATE_CONFIG_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateShadowTableConfig(@RequestBody TShadowTableConfigVo shadowTableConfigVo) {
        try {
            shadowTableConfigService.updateShadowTableConfig(shadowTableConfigVo);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("ShadowTableConfigController.updateShadowTableConfig 更新 影子表配置异常 {}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.updateShadowTableConfig 更新 影子表配置分页异常 {}", e);
            return ResponseError.create(1010100102, "更新影子表配置异常");
        }
    }

    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_DELETE_CONFIG_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> deleteShadowTableByIdList(@RequestParam("idList") String idList) {
        try {
            shadowTableConfigService.deleteShadowTableByIdList(idList);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("ShadowTableConfigController.deleteShadowTableByIdList 通过id列表删除影子表配置 {}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.deleteShadowTableByIdList 通过id列表删除影子表配置 {}", e);
            return ResponseError.create(1010100102, "更新影子表配置异常");
        }
    }

    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERYIPPORTNAME_CONFIG_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryApplicationDatabaseIpPortAndName(
            @RequestParam("applicationId") String applicationId) {
        try {
            return ResponseOk.create(shadowTableConfigService.queryApplicationDatabaseIpPortAndName(applicationId));
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.queryApplicationDatabaseIpPortAndName 查询应用 数据库IP端口表名异常 {}", e);
            return ResponseError.create(1010100102, "查询应用 数据库IP端口表名异常");
        }
    }

    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_ADD_CONFIG_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> saveShadowTableConfig(@RequestBody TShadowTableConfigVo shadowTableConfigVo) {
        try {
            shadowTableConfigService.saveShadowTableConfig(shadowTableConfigVo);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("ShadowTableConfigController.saveShadowTableConfig 保存影子表异常 {}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.saveShadowTableConfig 保存影子表异常 {}", e);
            return ResponseError.create(1010100102, "保存影子表异常");
        }
    }

    /**
     * 说明：API.01.08.007 通过应用id 获取 该应用对应的数据库ip端口
     *
     * @param applicationId 应用ID
     * @param dbName        数据库名称
     *
     * @return org.springframework.http.ResponseEntity<java.lang.Object>
     *
     * @author shulie
     * @create 2019/3/14 10:38
     */
    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_IPPORT_CONFIG_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryDatabaseIpPortList(@RequestParam("applicationId") String applicationId,
                                                          @RequestParam("dbName") String dbName) {
        try {
            return ResponseOk.create(shadowTableConfigService.queryDatabaseIpPortList(applicationId, dbName));
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.queryDatabaseIpPortList 查询应用 数据库IP端口异常 {}", e);
            return ResponseError.create(
                    TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_IPPORT_List_EXCEPTION.getErrorCode(),
                    TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_IPPORT_List_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明：API.01.08.008 通过应用id 获取 该应用对应的数据库ip端口
     *
     * @param applicationId 应用ID
     * @param ipPort        数据库的IP端口号
     *
     * @return org.springframework.http.ResponseEntity<java.lang.Object>
     *
     * @author shulie
     * @create 2019/3/14 10:38
     */
    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_DBNAME_CONFIG_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryDatabaseNameList(@RequestParam("applicationId") String applicationId,
                                                        @RequestParam("ipPort") String ipPort) {
        try {
            return ResponseOk.create(shadowTableConfigService.queryDatabaseNameList(applicationId, ipPort));
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.queryDatabaseNameList 查询应用数据库名称列表异常 {}", e);
            return ResponseError.create(
                    TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_DBNAME_List_EXCEPTION.getErrorCode(),
                    TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_DBNAME_List_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: API.01.01.002 查询应用列表信息接口
     *
     * @return 成功, 则返回应用信息列表, 失败则返回错误编码和错误信息
     *
     * @author shulie
     */
    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_SHADOWDB_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryShadowDatabaseApplicationList(@RequestBody Map<String, Object> paramMap) {
        try {
            return ResponseOk.create(shadowTableConfigService.queryShadowDatabaseApplicationList(paramMap));
        } catch (Exception e) {
            LOGGER.error("ApplicationMntController.queryApplicationinfo 查询异常", e);
            return ResponseError.create(1010100102, "查询应用信息异常");
        }
    }

    /**
     * 增加数据源
     *
     * @param tShadowTableDatasourceVo
     * @param bindingResult
     *
     * @return
     */
    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOW_DATASOURCE_SAVE_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> saveShadowDatasource(
            @RequestBody @Valid TShadowTableDatasourceVo tShadowTableDatasourceVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create(1050100401, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            shadowTableConfigService.saveShadowDatasource(tShadowTableDatasourceVo);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("ShadowTableConfigController.saveShadowTableConfig 新增影子数据源异常", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.saveShadowDatasource 新增影子数据源异常", e);
            return ResponseError.create(1010100102, "新增影子数据源异常");
        }
    }

    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOW_DATASOURCE_UPDATE_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateShadowDatasource(
            @RequestBody @Valid TShadowTableDatasourceVo tShadowTableDatasourceVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create(1050100401, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            shadowTableConfigService.updateShadowDatasource(tShadowTableDatasourceVo);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("ShadowTableConfigController.updateShadowDatasource 更新影子数据源异常 {}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.updateShadowDatasource 更新影子数据源异常{}", e);
            return ResponseError.create(1010100102, "更新影子数据源异常");
        }
    }

    /**
     * 获取 使用影子表 数据源的 库
     *
     * @return
     */
    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_SHADOW_DATASOURCE_GET_APPLICATION_URL,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> getDatasourceApplication(@RequestParam("useShadowTable") String useShadowTable) {
        try {
            return ResponseOk.create(shadowTableConfigService.getDatasourceApplication(useShadowTable));
        } catch (Exception e) {
            LOGGER.error("ShadowTableConfigController.updateShadowDatasource 更新影子数据源异常{}", e);
            return ResponseError.create(1010100102, "更新影子数据源异常");
        }
    }

}
