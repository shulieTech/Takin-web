package io.shulie.takin.web.entrypoint.controller.cloud.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import io.shulie.takin.adapter.api.entrypoint.resource.CloudResourceApi;
import io.shulie.takin.adapter.api.model.response.cloud.resources.CloudResource;
import io.shulie.takin.adapter.api.model.response.cloud.resources.Resource;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.service.cloud.server.resource.CloudResourcesService;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.web.common.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.shulie.takin.web.common.common.Response.PAGE_TOTAL_HEADER;
import static io.shulie.takin.web.common.common.Response.setHeaders;

@Slf4j
@RestController
@Api(tags = "压力机明细接口")
@RequestMapping("/api/cloud/resources")
public class CloudResourcesController extends AbstractIndicators {

    @Autowired
    private CloudResourceApi cloudResourceApi;

    @Autowired
    private CloudResourcesService cloudResourcesService;

    @Autowired
    private ReportDao reportDao;

    /**
     * @param taskId     启动压测任务后返回ID
     * @param resourceId 锁定资源后会返回资源ID
     * @return
     */
    @GetMapping("/getDetails")
    @ApiOperation("明细")
    public Response getDetails(
            @RequestParam(value = "jobId",required = false) Integer taskId,
            @RequestParam(value = "resourceId",required = false) String resourceId,
            @RequestParam(value = "sortField", required = false, defaultValue = "status") String sortField,
            @RequestParam(value = "sortType", required = false, defaultValue = "desc") String sortType,
            @RequestParam(value = "current", required = false) Integer currentPage,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        List<CloudResource> details = Lists.newArrayList();
        if (null != taskId && StringUtils.isNotBlank(resourceId)) {
            try {
                details = cloudResourceApi.getDetails(taskId, resourceId);
            } catch (Exception e) {
                log.error("压测及列表查询异常", e);
            }
        }
        Resource resource = cloudResourcesService.getDetail(details, taskId, resourceId, sortField, sortType, currentPage, pageSize);
        setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, String.valueOf(resource.getResources().size()));}});
        return Response.success(resource);
    }

    @PostMapping("forceRelease")
    @ApiOperation("强制释放资源")
    public void forceClose(String resourceId) {
        if (StringUtils.isNotBlank(resourceId)) {
            ReportResult report = reportDao.selectByResourceId(resourceId);
            if (Objects.nonNull(report) && Objects.equals(report.getStatus(), ReportConstants.FINISH_STATUS)) {
                releaseResource(resourceId);
            }
        }
    }
}
