package io.shulie.takin.web.entrypoint.controller.confcenter;

import javax.validation.Valid;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.domain.entity.TSecondLinkMnt;
import io.shulie.takin.web.biz.service.TSecondLinkMntService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
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
 * 说明: 二级链路管理接口
 *
 * @author shulie
 * @version v1.0
 * @create 2018/6/20 14:50
 */
@Api(tags = "二级链路管理接口")
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL)
public class SecondLinkMntController {
    private final Logger LOGGER = LoggerFactory.getLogger(SecondLinkMntController.class);

    @Autowired
    private TSecondLinkMntService secondLinkService;

    /**
     * 添加二级链路信息
     *
     * @param secondLinkMnt 二级链路信息
     * @param bindingResult 绑定结果验证
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     */
    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_ADD_SECOND_LINK_URI,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> saveSecondLink(@RequestBody @Valid TSecondLinkMnt secondLinkMnt,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create(1010300101, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            secondLinkService.saveSecondLink(secondLinkMnt);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("SecondLinkMntController.saveSecondLink 添加二级链路信息异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("SecondLinkMntController.saveSecondLink 添加二级链路信息异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_ADD_SECOND_LINK_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_ADD_SECOND_LINK_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 查询二级链路列表
     *
     * @param linkName     二级链路名称
     * @param baseLinkName 基础链路名称
     * @param pageNum      当前页码
     * @param pageSize     每页显示数目
     * @return 成功, 则返回链路信息列表, 失败则返回错误编码和错误信息
     */
    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_QUERY_SECOND_LINKLIST_URI,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryLinkList(@RequestParam("linkName") String linkName,
        @RequestParam("baseLinkName") String baseLinkName,
        @RequestParam("pageNum") Integer pageNum,
        @RequestParam("pageSize") Integer pageSize) {
        try {
            return ResponseOk.create(secondLinkService.queryLinkList(linkName, baseLinkName, pageNum, pageSize));
        } catch (TakinModuleException e) {
            LOGGER.error("SecondLinkMntController.queryLinkList 查询二级链路列表异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("SecondLinkMntController.queryLinkList 查询二级链路列表异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_SECOND_LINKLIST_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_QUERY_SECOND_LINKLIST_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 根据链路id查询二级链路信息详情
     *
     * @param linkId 链路id
     * @return 成功, 则返回链路详情, 失败则返回错误编码和错误信息
     */
    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_QUERY_SECOND_LINKINFO_URI,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryLinkByLinkId(@RequestParam("linkId") String linkId) {
        try {
            return ResponseOk.create(secondLinkService.queryLinkMapByLinkId(linkId));
        } catch (TakinModuleException e) {
            LOGGER.error("SecondLinkMntController.queryLinkByLinkId 查询二级链路详情异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("SecondLinkMntController.queryLinkByLinkId 查询二级链路详情异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_SECOND_LINKDETAIL_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_QUERY_SECOND_LINKDETAIL_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 删除二级链路信息
     *
     * @param linkIds 二级链路id列表，逗号分隔
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     */
    @GetMapping(value = APIUrls.API_TAKIN_CONFCENTER_DELETE_SECOND_LINKINFO_URI,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> deleteLinkByLinkIds(@RequestParam("linkIds") String linkIds) {
        try {
            secondLinkService.deleteLinkByLinkIds(linkIds);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("SecondLinkMntController.deleteLinkByLinkIds 删除链路信息异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("SecondLinkMntController.deleteLinkByLinkIds 删除链路信息异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_DELETE_SECOND_LINK_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_DELETE_SECOND_LINK_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 更新二级链路信息
     *
     * @param secondLinkMnt 二级链路信息
     * @param bindingResult 绑定结果验证
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     */
    @PostMapping(value = APIUrls.API_TAKIN_CONFCENTER_UPDATE_SECOND_LINKINFO_URI,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateLinkinfo(@RequestBody @Valid TSecondLinkMnt secondLinkMnt,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create(1010300101, bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            secondLinkService.updateLinkinfo(secondLinkMnt);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("SecondLinkMntController.updateLinkinfo 添加链路的服务缺失{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("SecondLinkMntController.updateLinkinfo 查询链路等级列表异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_UPDATE_SECOND_LINKINFO_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_UPDATE_SECOND_LINKINFO_EXCEPTION.getErrorMessage());
        }
    }

    /**
     *  这里只是测试使用，
     * @param linkId
     * @param linkLevel
     * @return
     */
    //    @GetMapping(value = "/queryApplicationInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    //    public ResponseEntity<Object> updateLinkinfo(@RequestParam("linkId") String linkId, @RequestParam
    //    ("linkLevel") String linkLevel) {
    //        try {
    //            return ResponseOk.create(secondLinkService.queryApplicationListByLinkInfo(linkId, linkLevel));
    //        } catch (TakinModuleException e) {
    //            LOGGER.error("SecondLinkMntController.updateLinkinfo 查询应用列表异常{}", e);
    //            return ResponseError.create(505, "查询应用列表异常!");
    //        }
    //    }

}
