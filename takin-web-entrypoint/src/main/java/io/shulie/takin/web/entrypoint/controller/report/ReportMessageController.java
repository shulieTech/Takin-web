package io.shulie.takin.web.entrypoint.controller.report;

import com.pamirs.takin.common.enums.ResponseResultEnum;
import com.pamirs.takin.common.enums.ResultCodeEnum;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageStatusCodeDTO;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageCodeReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageDetailReq;
import io.shulie.takin.web.biz.service.report.ReportMessageService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "请求报文")
public class ReportMessageController {

    @Resource
    private ReportMessageService reportMessageService;

    @GetMapping("vlt/report/message/code")
    @ApiOperation("LT版-请求报文-状态码列表")
    public Response<List<ReportMessageStatusCodeDTO>> getReportMessageStatusCodeList(ReportMessageCodeReq req) {
        List<ReportMessageStatusCodeDTO> codeList = reportMessageService.getStatusCodeList(req);
        //ResultCode转化为ResponseResult
        List<ReportMessageStatusCodeDTO> newCodeList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(codeList)) {
            Set<String> codeSet = new HashSet<>();
            for(ReportMessageStatusCodeDTO dto : codeList) {
                ResultCodeEnum codeEnum = ResultCodeEnum.getResultCodeEnumByCode(dto.getStatusCode());
                if(codeEnum == null) {
                    continue;
                }
                if(codeSet.contains(codeEnum.getResult().getCode())) {
                    continue;
                }
                codeSet.add(codeEnum.getResult().getCode());
                ReportMessageStatusCodeDTO newDto = new ReportMessageStatusCodeDTO();
                newDto.setStatusCode(codeEnum.getResult().getCode());
                newDto.setStatusName(codeEnum.getResult().getDesc());
            }
        }
        if(CollectionUtils.isEmpty(newCodeList)) {
            ReportMessageStatusCodeDTO defaultDTO = new ReportMessageStatusCodeDTO();
            defaultDTO.setStatusCode(ResponseResultEnum.RESP_SUCCESS.getCode());
            defaultDTO.setStatusName(ResponseResultEnum.RESP_SUCCESS.getDesc());
            newCodeList.add(defaultDTO);
        }
        return Response.success(newCodeList);
    }

    @GetMapping("vlt/report/message/detail")
    @ApiOperation("LT版-请求报文-请求明细")
    public Response<ReportMessageDetailDTO> getReportMessageDetail(ReportMessageDetailReq req) {
        List<String> resultCode = new ArrayList<>();
        //ResponseResult转化为ResultCode
        for(ResultCodeEnum resultCodeEnum : ResultCodeEnum.values()) {
            if(resultCodeEnum.getResult().getCode().equals(req.getStatusCode())) {
                resultCode.add(resultCodeEnum.getCode());
            }
        }
        req.setResultCode(StringUtils.join(resultCode, ","));
        return Response.success(reportMessageService.getOneTraceDetail(req));
    }
}
