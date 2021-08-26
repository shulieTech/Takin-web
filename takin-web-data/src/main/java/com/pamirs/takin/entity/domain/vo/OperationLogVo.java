package com.pamirs.takin.entity.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/9/24 10:42 上午
 */
@Data
@ApiModel(value = "OperationLogVo", description = "操作日志返参")
public class OperationLogVo {

    @ApiModelProperty(name = "module", value = "操作模块-主模块")
    private String module;

    @ApiModelProperty(name = "subModule", value = "操作模块-子模块")
    private String subModule;

    @ApiModelProperty(name = "type", value = "操作类型")
    private String type;

    @ApiModelProperty(name = "content", value = "操作内容描述")
    private String content;

    @ApiModelProperty(name = "userName", value = "操作人")
    private String userName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "startTime", value = "操作时间(开始时间)")
    private Date startTime;
}
