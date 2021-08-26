package com.pamirs.takin.entity.domain.vo.guardmanage;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 慕白
 * @date 2020-03-05 09:26
 */

@Data
@ApiModel(value = "LinkGuardVo", description = "挡板配置入参")
public class LinkGuardVo extends AuthQueryResponseCommonExt implements Serializable {
    private static final long serialVersionUID = -6566738117528313331L;

    @ApiModelProperty(name = "id", value = "挡板id")
    private Long id;
    @ApiModelProperty(name = "applicationName", value = "项目名称")
    private String applicationName;
    @ApiModelProperty(name = "applicationId", value = "项目id")
    private String applicationId;

    @ApiModelProperty(name = "methodInfo", value = "方法信息")
    private String methodInfo;
    @ApiModelProperty(name = "groovy", value = "流程消息体")
    private String groovy;

    @ApiModelProperty(name = "isEnable", value = "是否开启")
    private Boolean isEnable;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark ;
}
