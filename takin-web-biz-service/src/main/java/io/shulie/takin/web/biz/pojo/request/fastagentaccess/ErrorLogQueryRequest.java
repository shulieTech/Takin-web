package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import java.util.Date;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 异常日志查询对象
 *
 * @author ocean_wll
 * @date 2021/8/18 3:49 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参-异常日志列表查询对象")
public class ErrorLogQueryRequest extends PageBaseDTO {

    private static final long serialVersionUID = -6006643839660569892L;

    @ApiModelProperty(value = "agentId")
    private String agentId;

    @ApiModelProperty(value = "应用名")
    private String projectName;

    @ApiModelProperty(value = "日志关键词")
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endDate;
}
