package io.shulie.takin.web.entrypoint.controller.confcenter;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.domain.query.ApplicationQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationCreateRequest;
import io.shulie.takin.web.biz.service.ConfCenterService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.data.param.application.ApplicationCreateParam;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
 * 说明: 应用管理接口
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月13日
 */
@Slf4j
@Api(tags = "应用管理接口")
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
public class ApplicationMntController {

    @Resource
    private ConfCenterService confCenterService;

    /**
     * 说明: API.01.01.001 添加应用接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_ADD_APPLICATION_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveApplication(@RequestBody @Valid ApplicationCreateRequest tApplicationMnt,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseError.create(1010100101, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            ApplicationCreateParam param = new ApplicationCreateParam();
            BeanUtils.copyProperties(tApplicationMnt, param);
            confCenterService.saveApplication(param);
            //不想service嵌套service 特地 controller层做 以后可能facade层
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            log.error("ConfCenterController.queryWList 应用已经存在,请勿重新添加", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("ApplicationMntController.saveApplication 新增应用异常", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_ADD_APPLICATION_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_ADD_APPLICATION_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: API.01.01.002 查询应用列表信息接口
     *
     * @return 成功, 则返回应用信息列表, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_APPLICATIONINFO_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryApplicationInfo(@RequestBody ApplicationQueryRequest request) {
        try {

            return ResponseOk.create(confCenterService.queryApplicationList(request));
        } catch (Exception e) {
            log.error("ApplicationMntController.queryApplicationInfo 查询异常", e);
            return ResponseError.create(1010100102, "查询应用信息异常");
        }
    }

    /**
     * 说明: API.01.01.003 根据应用id查询应用信息详情接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_MODIFY_APPLICATIONINFO_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryApplicationInfoById(@RequestParam("applicationId") long applicationId) {
        if (StringUtils.isEmpty(String.valueOf(applicationId))) {
            return ResponseError.create(1010100301, "参数缺失");
        }
        try {
            return ResponseOk.create(confCenterService.queryApplicationInfoById(applicationId));
        } catch (Exception e) {
            log.error("ApplicationMntController.queryApplicationInfoById 查询异常", e);
            return ResponseError.create(1010100302, "根据应用id查询异常");
        }
    }

    /**
     * 说明: API.01.01.004 批量删除应用信息接口
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_DELETE_APPLICATIONINFO_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteApplicationInfoByIds(@RequestParam("applicationIds") String applicationIds) {

        try {
            String disableDeleteApplication = confCenterService.deleteApplicationInfoByIds(applicationIds);
            if (StringUtils.isNotEmpty(disableDeleteApplication)) {
                return ResponseError.create("该应用{ " + disableDeleteApplication + " }在基础链路中使用,不允许删除");
            } else {
                return ResponseOk.create("succeed");
            }
        } catch (Exception e) {
            log.error("ApplicationMntController.deleteApplicationInfoById 删除应用异常", e);
            return ResponseError.create(1010100102, "批量删除应用异常");
        }
    }

    /**
     * 说明: API.01.01.005 查询应用下拉框数据接口
     *
     * @return 成功, 则返回应用列表下拉框数据, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_APPLICATIONDATA_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryApplicationData() {

        try {
            return ResponseOk.create(confCenterService.queryApplicationdata());
        } catch (Exception e) {
            log.error("ConfCenterController.queryWList 查询应用下拉框数据异常", e);
            return ResponseError.create(1010100102, "查询应用下拉框数据异常");
        }
    }

    /**
     * 说明: API.01.01.006 根据应用id更新应用信息
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_UPDATE_APPLICATIONINFO_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateApplicationInfo(@RequestBody @Valid ApplicationCreateRequest tApplicationMnt,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create(1010100601, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            ApplicationCreateParam param = new ApplicationCreateParam();
            BeanUtils.copyProperties(tApplicationMnt, param);
            confCenterService.updateApplicationInfo(param);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            log.error("ConfCenterController.updateApplicationInfo 创建应用脚本存放路径不存在", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("ConfCenterController.updateApplicationInfo 根据应用id更新应用信息异常", e);
            return ResponseError.create(1010100602, "根据应用id更新应用信息异常");
        }
    }

    /**
     * 说明: API.01.01.007  查询应用信息列表(从pradar获取数据)
     *
     * @return 成功, 则返回应用信息列表, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_APPNAMELIST_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryAppNameByPradar() {
        try {
            String result = "{\"success\":\"true\",\"code\":200,\"data\":null}";
            return ResponseOk.create(result);
        } catch (Exception e) {
            log.error("ApplicationMntController.queryAppNameByPradar 查询异常", e);
            return ResponseError.create(1010100702, "查询应用信息异常");
        }
    }
}
