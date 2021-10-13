package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * agent版本管理(AgentVersion)controller 入参类
 *
 * @author liuchuan
 * @date 2021-08-11 19:43:33
 */
@ApiModel("入参类-发布新版本")
@Data
public class AgentVersionCreateRequest {

    /**
     * 大版本号
     */
    @ApiModelProperty(value = "大版本号", hidden = true)
    private String firstVersion;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号", required = true)
    @NotBlank(message = "版本号不能为空")
    private String version;

    /**
     * 文件存储路径
     */
    @ApiModelProperty(value = "文件存储路径", required = true)
    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    /**
     * 版本特性
     */
    @ApiModelProperty(value = "版本特性", required = true)
    @NotBlank(message = "版本特性不能为空")
    @Length(max = 2048, message = "版本特性最多输入2048个字符串")
    private String versionFeatures;

    /**
     * 当前版本是否已存在
     */
    @ApiModelProperty(value = "当前发布版本是否已存在，true:已存在，false:不存在", required = true)
    @NotNull(message = "当前发布版本是否已存在")
    private Boolean exist;

    /**
     * 新增的agent配置集合
     */
    @Valid
    @ApiModelProperty("agent配置信息")
    private List<AgentConfigCreateRequest> configList;
}
