package io.shulie.takin.web.entrypoint.controller.placeholder;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.placeholdermanage.PlaceholderManagePageRequest;
import io.shulie.takin.web.biz.pojo.request.placeholdermanage.PlaceholderManageRequest;
import io.shulie.takin.web.biz.pojo.response.placeholdermanage.PlaceholderManageResponse;
import io.shulie.takin.web.biz.service.placeholdermanage.PlaceholderManageService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 占位符管理
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "placeholderManage")
@Api(tags = "接口: 占位符管理")
public class PlaceholderManageController {

    @Resource
    private PlaceholderManageService placeholderManageService;

    @PostMapping
    @ApiOperation(value = "创建占位符")
    public ResponseResult<String> createPlaceholder(@RequestBody @Valid PlaceholderManageRequest createRequest){
        if (createRequest == null || StringUtils.isBlank(createRequest.getPlaceholderKey()) || StringUtils.isBlank(createRequest.getPlaceholderValue())){
            return ResponseResult.fail("创建占位符失败,缺失关键参数","");
        }
        placeholderManageService.createPlaceholder(createRequest);
        return ResponseResult.success("创建成功");
    }

    @PutMapping
    @ApiOperation(value = "修改占位符")
    public ResponseResult<String> updatePlaceholder(@RequestBody @Valid PlaceholderManageRequest request){
        if (request == null || request.getId() == null){
            return ResponseResult.fail("创建占位符失败,缺失关键参数","");
        }
        placeholderManageService.updatePlaceholder(request);
        return ResponseResult.success("修改成功");
    }

    @DeleteMapping
    @ApiOperation(value = "删除占位符")
    public ResponseResult<String> deletePlaceholder(@RequestBody @Valid PlaceholderManageRequest request){
        if (request == null || request.getId() == null){
            return ResponseResult.fail("删除占位符失败,缺失关键参数","");
        }
        placeholderManageService.deletePlaceholder(request.getId());
        return ResponseResult.success("修改成功");
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询占位符")
    public PagingList<PlaceholderManageResponse> listPlaceholder(PlaceholderManagePageRequest request){
        return placeholderManageService.listPlaceholder(request);
    }

}
