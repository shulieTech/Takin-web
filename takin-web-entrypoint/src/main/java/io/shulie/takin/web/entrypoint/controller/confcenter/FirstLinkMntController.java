package io.shulie.takin.web.entrypoint.controller.confcenter;

import java.util.List;

import javax.validation.Valid;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.domain.entity.TFirstLinkMnt;
import io.shulie.takin.web.biz.service.TFirstLinkMntService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
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
 * 说明: 一级链路管理接口
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月16日
 */
@Api(tags = "一级链路管理接口")
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
public class FirstLinkMntController {
    private final Logger LOGGER = LoggerFactory.getLogger(FirstLinkMntController.class);

    @Autowired
    private TFirstLinkMntService firstLinkService;

    /**
     * 说明: 添加一级链路信息
     *
     * @param firstLinkMnt  一级链路信息
     * @param bindingResult 绑定的结果
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_ADD_FIRST_LINK_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveLink(@RequestBody @Valid TFirstLinkMnt firstLinkMnt,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create("1010300101", bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            firstLinkService.saveLink(firstLinkMnt);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("FirstLinkMntController.saveLink 添加一级链路信息异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("FirstLinkMntController.saveLink 添加一级链路信息异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_ADD_FIRST_LINK_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_ADD_FIRST_LINK_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: 查询一级链路信息列表
     *
     * @param firstLinkName  一级链路名称
     * @param secondLinkName 二级链路名称
     * @param pageNum        当前页码
     * @param pageSize       每页显示数目
     * @return 成功, 则返回链路信息列表, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_FIRST_LINKLIST_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryLinkList(@RequestParam("firstLinkName") String firstLinkName,
        @RequestParam("secondLinkName") String secondLinkName,
        @RequestParam("pageNum") Integer pageNum,
        @RequestParam("pageSize") Integer pageSize
    ) {
        try {
            return ResponseOk.create(firstLinkService.queryLinkList(firstLinkName, secondLinkName, pageNum, pageSize));
        } catch (TakinModuleException e) {
            LOGGER.error("FirstLinkMntController.queryLinkList 查询一级链路列表异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("FirstLinkMntController.queryLinkList 查询一级链路列表异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_FIRST_LINKLIST_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_QUERY_FIRST_LINKLIST_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: 根据一级链路id查询链路信息详情接口
     *
     * @param linkId 链路id
     * @return 成功, 则返回链路详情, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_FIRST_LINKINFO_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryLinkByLinkId(@RequestParam("linkId") String linkId) {
        if (StringUtils.isEmpty(linkId)) {
            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_FIRST_LINKINFO_PARAMLACK.getErrorCode(),
                TakinErrorEnum.CONFCENTER_QUERY_FIRST_LINKINFO_PARAMLACK.getErrorMessage());
        }
        try {
            return ResponseOk.create(firstLinkService.queryLinkMapByLinkId(linkId));
        } catch (TakinModuleException e) {
            LOGGER.error("FirstLinkMntController.queryLinkByLinkId 查询一级链路详情异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("FirstLinkMntController.queryLinkByLinkId 查询一级链路详情异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_FIRST_LINKDETAIL_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_QUERY_FIRST_LINKDETAIL_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 删除一级链路信息接口
     *
     * @param linkIds 链路id列表，逗号分隔
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_DELETE_FIRST_LINKINFO_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteLinkByLinkIds(@RequestParam("linkIds") String linkIds) {
        try {
            firstLinkService.deleteLinkByLinkIds(linkIds);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("FirstLinkMntController.deleteLinkByLinkIds 删除链路信息异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("FirstLinkMntController.deleteLinkByLinkIds 删除链路信息异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_DELETE_FIRST_LINK_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_DELETE_FIRST_LINK_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: 根据id更新链路信息接口
     *
     * @param firstLinkMnt  一级链路信息
     * @param bindingResult 绑定结果验证
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @PostMapping(value = ApiUrls.API_TAKIN_CONFCENTER_UPDATE_FIRST_LINKINFO_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateLinkinfo(@RequestBody @Valid TFirstLinkMnt firstLinkMnt,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseError.create("1010300101", bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            firstLinkService.updateLinkinfo(firstLinkMnt);
            return ResponseOk.create("succeed");
        } catch (TakinModuleException e) {
            LOGGER.error("FirstLinkMntController.updateLinkinfo 更新一级链路信息异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("FirstLinkMntController.updateLinkinfo 更新一级链路信息异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_UPDATE_FIRST_LINKINFO_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_UPDATE_FIRST_LINKINFO_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: 根据一级链路id获取链路拓扑图
     *
     * @param linkId 链路id
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_QUERY_LINK_TOPOLOGY_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLinkTopologyByFirstLinkId(@RequestParam("linkId") String linkId) {
        try {
            return ResponseOk.create(firstLinkService.getLinkTopologyByFirstLinkId(linkId));
        } catch (TakinModuleException e) {
            LOGGER.error("FirstLinkMntController.getLinkTopologyByFirstLinkId 获取链路拓扑图异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.error("FirstLinkMntController.getLinkTopologyByFirstLinkId 获取链路拓扑图异常{}", e);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_QUERY_LINK_TOPOLOGY_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_QUERY_LINK_TOPOLOGY_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * 说明: 判断某一个二级链路是否有对应的一级链路
     *
     * @param secondLinkId 二级链路id
     * @return 成功, 则返回成功信息, 失败则返回错误编码和错误信息
     * @author shulie
     */
    @GetMapping(value = ApiUrls.API_TAKIN_CONFCENTER_EXIST_FIRST_LINK_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> existFirstLinkBySecondLinkId(@RequestParam("secondLinkId") String secondLinkId) {
        if (StringUtils.isEmpty(secondLinkId)) {
            LOGGER.error("FirstLinkMntController.existFirstLinkBySecondLinkId接收了非法的参数：" + secondLinkId);
            return ResponseError.create(TakinErrorEnum.CONFCENTER_SECOND_LINKID_LIST_IS_NULL_EXCEPTION.getErrorCode(),
                TakinErrorEnum.CONFCENTER_SECOND_LINKID_LIST_IS_NULL_EXCEPTION.getErrorMessage());
        }
        List<TFirstLinkMnt> tFirstLinkMnts = firstLinkService.queryLinkBySecondLinkId(secondLinkId);
        if (tFirstLinkMnts != null && tFirstLinkMnts.size() > 0) {
            return ResponseOk.create(true);
        } else {
            return ResponseOk.create(false);
        }
    }

    @GetMapping(value = "/emptyUrl", produces = MediaType.APPLICATION_JSON_VALUE)
    public String emptyUrl() {
        return "success";
    }

}
