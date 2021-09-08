package io.shulie.takin.web.entrypoint.controller.leakcheck;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.biz.service.LeakSqlService;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlBatchRefsRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlCreateRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlDetailRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.SqlTestRequest;
import io.shulie.takin.web.biz.pojo.response.leakcheck.LeakSqlBatchRefsResponse;
import io.shulie.takin.web.biz.pojo.response.leakcheck.LeakSqlRefsResponse;
import io.shulie.takin.web.biz.pojo.response.leakcheck.SqlTestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
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
 * @author shiyajian
 * create: 2020-12-28
 */
@RestController
@RequestMapping("/api/leak/sql")
@Api(tags = "漏数配置管理")
public class LeakCheckSqlController {

    @Autowired
    private LeakSqlService leakSqlService;

    @PostMapping("/create")
    @ApiOperation("创建脚本")
    public void createLeakSqlRef(@Validated @RequestBody LeakSqlCreateRequest createRequest) {
        leakSqlService.createLeakCheckConfig(createRequest);
    }

    @PutMapping("/update")
    @ApiOperation("更新脚本")
    public void updateLeakSqlRef(@Validated @RequestBody LeakSqlUpdateRequest updateRequest) {
        leakSqlService.updateLeakCheckConfig(updateRequest);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除脚本")
    public void deleteLeakSqlRef(@Validated @RequestBody LeakSqlDeleteRequest deleteRequest) {
        leakSqlService.deleteLeakCheckConfig(deleteRequest);
    }

    @GetMapping("/detail")
    @ApiOperation("脚本详情")
    public LeakSqlRefsResponse getLeakSqlRef(@Validated LeakSqlDetailRequest detailRequest) {
        return leakSqlService.getLeakCheckConfigDetail(detailRequest);
    }

    @PostMapping("/batch")
    @ApiOperation("批量查询")
    public List<LeakSqlBatchRefsResponse> getBatchLeakSqlRef(@Validated @RequestBody LeakSqlBatchRefsRequest refsRequest) {
        return leakSqlService.getBatchLeakCheckConfig(refsRequest);
    }

    @GetMapping("/scene")
    @ApiOperation("查询场景下是否配置漏数脚本")
    public Boolean getSceneLeakConfig(@NotNull Long sceneId) {
        return leakSqlService.getSceneLeakConfig(sceneId);
    }

    @PostMapping("/test")
    @ApiOperation("sql调试")
    public SqlTestResponse testDatasource(@Validated @RequestBody SqlTestRequest sqlTestRequest) {
        String errorMessage = leakSqlService.testSqlConnection(sqlTestRequest);
        SqlTestResponse response = new SqlTestResponse();
        response.setErrorMessage(errorMessage);
        response.setPassed(StringUtils.isEmpty(errorMessage));
        return response;
    }
}
