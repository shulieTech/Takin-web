package io.shulie.takin.web.biz.pojo.request.linkmanage;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@ApiModel("业务流程列表查询--入参")
public class BusinessFlowPageQueryRequest extends PagingDevice {

    @ApiModelProperty("业务流程名称")
    private String businessFlowName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate stopTime;
}
