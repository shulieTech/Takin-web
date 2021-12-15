package io.shulie.takin.web.entrypoint.controller.domain;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainCreateRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainQueryRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.domain.BusinessDomainListResponse;
import io.shulie.takin.web.biz.service.domain.BusinessDomainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务域表(BusinessDomain)controller
 *
 * @author liuchuan
 * @date 2021-12-07 16:12:19
 */
@RestController
@RequestMapping("/api/domain")
@Api(tags = "业务域管理")
public class BusinessDomainController {

    @Autowired
    private BusinessDomainService businessDomainService;

    @PostMapping("/create")
    @ApiOperation(value = "创建业务域")
    public void create(@Validated @RequestBody BusinessDomainCreateRequest createRequest) {
        businessDomainService.create(createRequest);
    }

    @PutMapping("/update")
    @ApiOperation("更新业务域")
    public void update(@Validated @RequestBody BusinessDomainUpdateRequest updateRequest) {
        businessDomainService.update(updateRequest);
    }

    @DeleteMapping("/delete/batch")
    @ApiOperation("批量删除业务域")
    public void deleteBatch(@Validated @RequestBody BusinessDomainDeleteRequest deleteRequest) {
        businessDomainService.delete(deleteRequest);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除单个业务域")
    public void delete(@Validated @NotNull Long id) {
        businessDomainService.deleteById(id);
    }

    @PostMapping("/list")
    @ApiOperation("业务域列表")
    public PagingList<BusinessDomainListResponse> list(
        @Validated @RequestBody BusinessDomainQueryRequest queryRequest) {
        return businessDomainService.list(queryRequest);
    }

    @GetMapping("/list/dictionary")
    @ApiOperation("业务域列表(下拉框筛选业务域)")
    public List<BusinessDomainListResponse> listDomainNoPage() {
        return businessDomainService.listNoPage();
    }
}
