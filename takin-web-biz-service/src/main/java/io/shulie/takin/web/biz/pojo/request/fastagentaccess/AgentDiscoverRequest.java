package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/27 11:06 上午
 */
@Data
@ApiModel("入参-探针列表查询类")
public class AgentDiscoverRequest {

    @ApiModelProperty(value = "应用名", required = true)
    @NotBlank(message = "应用名不能为空")
    private String projectName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "传的值为下载installAgent.sh的时间")
    @NotNull(message = "date不能为null")
    private Date downloadScriptDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "点击检测的时间")
    @NotNull(message = "date不能为null")
    private Date checkDate;
}
