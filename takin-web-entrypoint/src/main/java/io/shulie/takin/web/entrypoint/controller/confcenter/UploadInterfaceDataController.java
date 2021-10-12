//package io.shulie.takin.web.entrypoint.controller.confcenter;
//
//import com.pamirs.takin.common.ResponseError;
//import com.pamirs.takin.common.ResponseOk;
//import com.pamirs.takin.common.constant.TakinErrorEnum;
//import com.pamirs.takin.common.exception.TakinModuleException;
//import com.pamirs.takin.entity.domain.vo.TUploadInterfaceVo;
//import com.pamirs.takin.entity.domain.vo.TUploadNeedVo;
//import io.shulie.takin.web.common.constant.ApiUrls;
//import io.swagger.annotations.Api;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 说明: 二级链路管理接口
// *
// * @author shulie
// * @version v1.0
// * @create 2018/6/20 14:50
// */
//@Api(tags = "二级链路管理接口")
//@RestController
//@RequestMapping(ApiUrls.takin_API_URL)
//public class UploadInterfaceDataController {
//
//    private final Logger LOGGER = LoggerFactory.getLogger(UploadInterfaceDataController.class);
//
//    @Autowired
//    private UploadInterfaceService uploadInterfaceService;
//
//    /**
//     * 判断是否需要上传
//     *
//     * @param uploadNeedVo appName与数量
//     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
//     */
//    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_INTERFACE_NEED_UPLOAD_URL,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> judgeNeedUpload(@RequestBody TUploadNeedVo uploadNeedVo) {
//        try {
//            return ResponseOk.create(uploadInterfaceService.executeNeedUploadInterface(uploadNeedVo));
//        } catch (TakinModuleException e) {
//            LOGGER.error("UploadInterfaceDataController.judgeNeedUpload 查询是否需要上传异常{}", e);
//            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
//        } catch (Exception e) {
//            LOGGER.error("UploadInterfaceDataController.judgeNeedUpload 查询是否需要上传异常{}", e);
//            return ResponseError.create(TakinErrorEnum.CONFCENTER_INTERFACE_NEED_UPLOAD_EXCEPTION.getErrorCode(),
//                TakinErrorEnum.CONFCENTER_INTERFACE_NEED_UPLOAD_EXCEPTION.getErrorMessage());
//        }
//    }
//
//    /**
//     * 判断是否需要上传
//     *
//     * @param TUploadInterfaceVo appName与接口信息
//     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
//     */
//    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_INTERFACE_UPLOAD_URL,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> judgeNeedUpload(@RequestBody TUploadInterfaceVo TUploadInterfaceVo) {
//        try {
//            return ResponseOk.create(uploadInterfaceService.saveUploadInterfaceData(TUploadInterfaceVo));
//        } catch (TakinModuleException e) {
//            LOGGER.error("UploadInterfaceDataController.judgeNeedUpload 查询是否需要上传异常{}", e);
//            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
//        } catch (Exception e) {
//            LOGGER.error("UploadInterfaceDataController.judgeNeedUpload 查询是否需要上传异常{}", e);
//            return ResponseError.create(TakinErrorEnum.CONFCENTER_INTERFACE_UPLOAD_EXCEPTION.getErrorCode(),
//                TakinErrorEnum.CONFCENTER_INTERFACE_NEED_UPLOAD_EXCEPTION.getErrorMessage());
//        }
//    }
//}
