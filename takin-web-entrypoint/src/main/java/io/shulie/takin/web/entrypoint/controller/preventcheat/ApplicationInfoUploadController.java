package io.shulie.takin.web.entrypoint.controller.preventcheat;

import java.util.Map;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinDictTypeEnum;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceVo;
import io.shulie.takin.web.biz.service.ApplicationInfoUploadService;
import io.shulie.takin.web.biz.service.ConfCenterService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ApplicationInfoUpload")
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
public class ApplicationInfoUploadController {

    private final Logger LOGGER = LoggerFactory.getLogger(ApplicationInfoUploadController.class);

    @Autowired
    private ApplicationInfoUploadService applicationInfoUploadService;

    @Autowired
    private ConfCenterService confCenterService;

    /**
     * 说明:  API.06.02.001 应用信息上传 上传可能作弊 与 SQL 解析异常 以后还可以上传其他的
     *
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     * @date 2019/3/28 20:10
     */
    @PostMapping(value = ApiUrls.API_TAKIN_APPLICATION_INFO_UPLOAD_UPLOAD,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> applicationInfoUpload(@RequestBody TUploadInterfaceVo uploadInterfaceVo) {
        try {
            applicationInfoUploadService.saveApplicationInfoUpload(uploadInterfaceVo);
            return ResponseOk.create("上传成功");
        } catch (Exception e) {
            LOGGER.error("ApplicationInfoUploadController.applicationInfoUpload 上传信息失败 {}", e);
            return ResponseError.create("上传失败");
        }
    }

    /**
     * API.06.02.002 上传信息查询分页
     *
     * @return
     */
    @PostMapping(value = ApiUrls.API_TAKIN_APPLICATION_INFO_UPLOAD_QUERY_PAGE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> applicationInfoUploadQueryPage(@RequestBody Map<String, Object> paramMap) {
        try {
            return ResponseOk.create(applicationInfoUploadService.queryApplicationPage(paramMap));
        } catch (Exception e) {
            LOGGER.error("ApplicationInfoUploadController.applicationInfoUploadQueryPage 查询分页信息失败 {}", e);
            return ResponseError.create("查询分页信息失败");
        }
    }

    /**
     * 说明: API.06.02.003 上传信息类型字典列表
     *
     * @return 成功, 防御上传信息里字典列表, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_APPLICATION_INFO_UPLOAD_INFO_TYPE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryInfoType() {
        try {
            return ResponseOk.create(confCenterService.queryDicList(TakinDictTypeEnum.UPLOAD_INFO_TYPE));
        } catch (Exception e) {
            LOGGER.error("ApplicationInfoUploadController.queryInfoType 查询上传信息类型{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_LINKTYPE_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_QUERY_LINKTYPE_EXCEPTION.getErrorMessage());
        }
    }

}
