package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel("出参类-脚本列表出参")
public class ScriptManageDeployResponse extends AuthQueryResponseCommonExt implements Serializable {
    private static final long serialVersionUID = 2024459580186953657L;

    /**
     * 脚本发布id
     */
    @ApiModelProperty(value = "脚本发布实例id")
    private Long id;

    @ApiModelProperty(value = "脚本id")
    private Long scriptId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "脚本名称")
    @JsonProperty("scriptName")
    private String name;

    /**
     * 关联类型(业务活动)
     */
    @ApiModelProperty(value = "关联类型(业务活动)")
    @JsonProperty("type")
    private String refType;

    /**
     * 关联值名称（此处为关联活动名称或关联流程名称）
     */
    @ApiModelProperty(value = "关联值名称（此处为关联活动名称或关联流程名称）")
    @JsonProperty("relatedBusiness")
    private String refName;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updateTime")
    private Date gmtUpdate;

    /**
     * 脚本版本
     */
    @ApiModelProperty(value = "脚本版本")
    private Integer scriptVersion;

    /**
     * 文件列表
     */
    @ApiModelProperty(value = "文件列表")
    @JsonProperty("relatedFiles")
    private List<FileManageResponse> fileManageResponseList;

    /**
     * 标签列表
     */
    @ApiModelProperty(value = "标签列表")
    @JsonProperty("tag")
    private List<TagManageResponse> tagManageResponses;

    /**
     * 只有一个脚本实例
     */
    @ApiModelProperty(value = "只有一个脚本实例")
    @JsonProperty("onlyOne")
    private Boolean onlyOne = true;
}
