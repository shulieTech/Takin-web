package io.shulie.takin.web.entrypoint.controller;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.output.probe.CreateProbeOutput;
import io.shulie.takin.web.biz.pojo.output.probe.ProbeListOutput;
import io.shulie.takin.web.biz.pojo.request.probe.CreateProbeRequest;
import io.shulie.takin.web.biz.service.ProbeService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 探针包表(Probe)表控制层
 *
 * @author liuchuan
 * @since 2021-06-03 13:40:57
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "probe")
@Api(tags = "接口: 探针")
public class ProbeController {

    @Autowired
    private ProbeService probeService;

    @ApiOperation("|_ 列表, 分页")
    @GetMapping("/list")
    public PagingList<ProbeListOutput> index(@ApiIgnore PageBaseDTO pageBaseDTO) {
        return probeService.pageProbe(pageBaseDTO);
    }

    @ApiOperation("|_ 创建")
    @PostMapping
    public CreateProbeOutput create(@Validated @RequestBody CreateProbeRequest createProbeRequest) {
        return probeService.create(createProbeRequest.getProbePath());
    }

}
