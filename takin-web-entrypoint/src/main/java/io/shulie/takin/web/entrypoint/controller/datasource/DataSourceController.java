package io.shulie.takin.web.entrypoint.controller.datasource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceCreateRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceQueryRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceTestRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceUpdateTagsRequest;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceDetailResponse;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceDictionaryResponse;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceListResponse;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceRefsResponse;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceTestResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.biz.service.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据源管理控制器
 */
@RestController
@RequestMapping("/api/datasource")
@Api(tags = "数据源管理")
public class DataSourceController {

    @Autowired
    private DataSourceService datasourceService;

    @PostMapping("/create")
    @ApiOperation(value = "创建数据源")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.DATASOURCE_MANAGE,
            needAuth = ActionTypeEnum.CREATE
    )
    public void createDatasource(@Validated @RequestBody DataSourceCreateRequest createRequest) {
        datasourceService.createDatasource(createRequest);
    }

    @PutMapping("/update")
    @ApiOperation("更新数据源")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.DATASOURCE_MANAGE,
            needAuth = ActionTypeEnum.UPDATE
    )
    public void updateDatasource(@Validated @RequestBody DataSourceUpdateRequest updateRequest) {
        datasourceService.updateDatasource(updateRequest);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除数据源")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.DATASOURCE_MANAGE,
            needAuth = ActionTypeEnum.DELETE
    )
    public void deleteDatasource(@Validated @RequestBody DataSourceDeleteRequest request) {
        datasourceService.deleteDatasource(request.getDatasourceIds());
    }

    @PostMapping("/list")
    @ApiOperation("数据源列表")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.DATASOURCE_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<DatasourceListResponse> listDatasource(@Validated @RequestBody DataSourceQueryRequest queryRequest) {
        PagingList<DatasourceListResponse> results = datasourceService.listDatasource(queryRequest);
        return results;
    }

    @GetMapping("/list/dictionary")
    @ApiOperation("数据源列表(下拉框筛选数据源)")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.DATASOURCE_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<DatasourceDictionaryResponse> listDatasourceNoPage() {
        return datasourceService.listDatasourceNoPage();
    }

    @GetMapping("/detail")
    @ApiOperation("数据源详情")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.DATASOURCE_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public DatasourceDetailResponse getDatasourceById(@Valid @NotNull Long datasourceId) {
        DatasourceDetailResponse response = datasourceService.getDatasource(datasourceId);
        return response;
    }

    @GetMapping("/tags")
    @ApiOperation("数据源标签列表")
    public List<TagManageResponse> getDatasourceTags() {
        return datasourceService.getDatasourceTags();
    }

    @PostMapping("/tags")
    @ApiOperation("保存数据源标签")
    public void updateDatasourceTags(@Validated @RequestBody DataSourceUpdateTagsRequest tagsRequest) {
        datasourceService.updateDatasourceTags(tagsRequest);
    }

    @PostMapping("/test")
    @ApiOperation("数据源调试")
    public DatasourceTestResponse testDatasource(@Validated @RequestBody DataSourceTestRequest testRequest) {
        String errorMessage = datasourceService.testConnection(testRequest);
        DatasourceTestResponse response = new DatasourceTestResponse();
        response.setErrorMessage(errorMessage);
        response.setPassed(StringUtils.isEmpty(errorMessage));
        return response;
    }

    @GetMapping("/refs")
    @ApiOperation("数据源绑定的实体列表")
    public List<DatasourceRefsResponse> getDatasourceRefs(@Valid @NotNull @RequestParam Long datasourceId) {
        List<String> names = datasourceService.getBizActivitiesName(datasourceId);
        return names.stream().map(name -> {
            DatasourceRefsResponse datasourceRefsResponse = new DatasourceRefsResponse();
            datasourceRefsResponse.setRefName(name);
            return datasourceRefsResponse;
        }).collect(Collectors.toList());
    }
}
