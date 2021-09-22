package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * agent版本管理(AgentVersion)controller 详情响应类
 *
 * @author liuchuan
 * @date 2021-08-11 19:43:22
 */
@ApiModel("出参类-详情出参")
@Data
public class AgentVersionListResponse {

    @ApiModelProperty("id")
    private Long id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date gmtUpdate;

    /**
     * 操作人
     */
    @ApiModelProperty("操作人")
    private String operator;

    /**
     * 大版本号
     */
    @ApiModelProperty("大版本号")
    private String firstVersion;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private String version;

    /**
     * 版本特性
     */
    @ApiModelProperty("版本特性")
    private String versionFeatures;

}
