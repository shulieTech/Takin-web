package io.shulie.takin.web.entrypoint.controller.whitelist;

/**
 * 白名单列表，并非应用内的白名单。
 * 应用内的白名单列表，查看
 *
 * @author 无涯
 * @date 2021/4/13 3:28 下午
 * @see io.shulie.takin.web.entrypoint.controller.linkmanage.WhiteListConfigController
 */

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.service.linkmanage.WhiteListService;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistSearchInput;
import io.shulie.takin.web.biz.pojo.request.whitelist.WhitelistSearchRequest;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.web.common.vo.whitelist.WhiteListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "whitelist")
@Api(tags = "白名单列表", value = "白名单管理接口")
public class WhitelistController {
    @Autowired
    private WhiteListService whiteListService;

    @ApiOperation("全量白名单分页接口")
    @GetMapping("/list")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPWHITELIST,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<WhiteListVO> pagingList(WhitelistSearchRequest request) {
        WhitelistSearchInput input = new WhitelistSearchInput();
        BeanUtils.copyProperties(request, input);
        return whiteListService.pagingList(input);
    }

}
