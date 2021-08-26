package io.shulie.takin.web.biz.pojo.response.application;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 */
@Data
@ApiModel("出参类-应用下的节点出参类")
public class ApplicationNodeResponse {

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("进程号")
    @JsonProperty("processNumber")
    private String processNo;

    @ApiModelProperty("agent语言")
    @JsonProperty("agentLang")
    private String agentLanguage;

    @ApiModelProperty("agent版本")
    private String agentVersion;

    @ApiModelProperty("更新时间")
    private String updateTime;

    /**
     * ip地址
     */
    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("探针版本")
    private String probeVersion;

    @ApiModelProperty("探针状态, 0 未安装, 1 安装中, 2 已安装, 11 升级中, 21 卸载中")
    private Integer probeStatus;

    @ApiModelProperty("探针状态文字描述")
    private String probeStatusDesc = "";

    @ApiModelProperty("操作状态描述, 您上次未安装成功, 您上次未升级成功, 您上次未卸载成功")
    private String operateStatusDesc = "";

    /**
     * ip地址
     */
    @ApiModelProperty(hidden = true)
    private String ipAddress;

    /**
     * 探针最后修改时间
     */
    @ApiModelProperty(hidden = true)
    private Date agentUpdateTime;

    /**
     * 进程号
     */
    @ApiModelProperty(hidden = true)
    private String progressId;

}
