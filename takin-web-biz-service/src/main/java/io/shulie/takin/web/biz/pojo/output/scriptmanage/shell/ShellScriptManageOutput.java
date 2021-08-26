package io.shulie.takin.web.biz.pojo.output.scriptmanage.shell;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.output.tagmanage.TagManageOutput;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/8 7:59 下午
 */
@Data
public class ShellScriptManageOutput extends AuthQueryResponseCommonExt implements Serializable {
    private static final long serialVersionUID = 2024459580186953657L;

    /**
     * 脚本发布id
     */
    @ApiModelProperty(value = "脚本发布实例id")
    private Long scripDeployId;

    @ApiModelProperty(value = "脚本id")
    private Long scriptId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "脚本名称")
    @JsonProperty("scriptName")
    private String name;

    /**
     * 标签列表
     */
    @ApiModelProperty(value = "标签列表")
    @JsonProperty("tag")
    private List<TagManageOutput> tagManageOutputs;

    /**
     * 脚本版本
     */
    @ApiModelProperty(value = "脚本版本")
    private Integer scriptVersion;

    /**
     * 脚本版本
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updateTime")
    private Date gmtUpdate;
    /**
     * 是否运行中
     */
    @ApiModelProperty(value = "是否运行")
    @JsonProperty("execute")
    private Boolean execute = false;

}
