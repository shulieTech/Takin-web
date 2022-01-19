package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-03-16 15:03
 */

@Data
@ApiModel(value = "ApplicationVo", description = "应用配置入参")
public class ApplicationVo extends AuthQueryResponseCommonExt implements Serializable, AppConstants {
    private static final long serialVersionUID = 412834588202338300L;

    @ApiModelProperty("应用主键id")
    private Long primaryKeyId;

    @ApiModelProperty(name = "id", value = "项目id")
    private String id;

    @NotBlank(message = "应用名称" + MUST_BE_NOT_NULL)
    @ApiModelProperty(name = "applicationName", value = "项目名称")
    private String applicationName;

    @ApiModelProperty(name = "accessStatus", value = "接入状态； 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常 ")
    private Integer accessStatus;

    @ApiModelProperty(name = "nodeNum", value = "节点数量")
    private Integer nodeNum;

    @ApiModelProperty(name = "exceptionInfo", value = "异常信息")
    private String exceptionInfo;

    @ApiModelProperty(name = "switchStatus", value = "开关状态")
    private String switchStutus;

    @ApiModelProperty(name = "pressureEnable", value = "压测开关，true：开")
    private Boolean pressureEnable = true;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(name = "applicationDesc", value = "项目信息")
    private String applicationDesc;

    @NotBlank(message = "影子库表结构脚本路径" + MUST_BE_NOT_NULL)
    @ApiModelProperty(name = "ddlScriptPath", value = "影子库表结构脚本路径")
    private String ddlScriptPath;

    @NotBlank(message = "数据清理脚本路径" + MUST_BE_NOT_NULL)
    @ApiModelProperty(name = "cleanScriptPath", value = "数据清理脚本路径")
    private String cleanScriptPath;

    @NotBlank(message = "基础数据准备脚本路径" + MUST_BE_NOT_NULL)
    @ApiModelProperty(name = "readyScriptPath", value = "基础数据准备脚本路径")
    private String readyScriptPath;

    @NotBlank(message = "铺底数据脚本路径" + MUST_BE_NOT_NULL)
    @ApiModelProperty(name = "basicScriptPath", value = "铺底数据脚本路径")
    private String basicScriptPath;

    @NotBlank(message = "缓存预热脚本地址" + MUST_BE_NOT_NULL)
    @ApiModelProperty(name = "cacheScriptPath", value = "缓存预热脚本地址")
    private String cacheScriptPath;

    @ApiModelProperty(name = "silenceEnable", value = "静默开关，true：开")
    private Boolean silenceEnable = true;
}
