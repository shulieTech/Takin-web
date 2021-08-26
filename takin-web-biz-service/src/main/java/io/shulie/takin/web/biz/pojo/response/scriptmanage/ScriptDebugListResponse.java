package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 1:44 下午
 */
@Data
@ApiModel("出参类-脚本调试列表出参")
public class ScriptDebugListResponse {

    @ApiModelProperty("脚本调试记录id")
    private Long id;

    @ApiModelProperty(value = "脚本调试记录状态, 0 未启动, 1 启动中, 2 请求中, 3 请求结束, 4 调试成功, 5 调试失败", example = "0")
    private Integer status;

    @ApiModelProperty("备注, 当调试失败时, 失败信息")
    private String remark;

    @ApiModelProperty("对应的 cloud 报告id")
    private Long cloudReportId;

    @ApiModelProperty("请求条数")
    private Integer requestNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("调试时间")
    private Date createdAt;

}
