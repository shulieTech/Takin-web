package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import java.util.List;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * agent配置管理(AgentConfig)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-08-12 18:54:53
 */
@ApiModel("入参类-agent配置创建类")
@Data
public class AgentConfigCreateRequest {

    /**
     * 配置类型0：全局配置，1：应用配置
     *
     * @see AgentConfigTypeEnum
     */
    @ApiModelProperty(value = "配置类型0：全局配置，1：应用配置，默认是0:全局配置", hidden = true)
    private Integer type;

    /**
     * 中文配置key
     */
    @ApiModelProperty(value = "中文配置key", required = true)
    @NotBlank(message = "中文配置key不能为空")
    private String zhKey;

    /**
     * 英文配置key
     */
    @ApiModelProperty(value = "英文配置key", required = true)
    @NotBlank(message = "英文配置key不能为空")
    private String enKey;

    /**
     * 配置默认值
     */
    @ApiModelProperty(value = "默认值", required = true)
    @NotBlank(message = "默认值不能为空")
    private String defaultValue;

    /**
     * 配置描述
     */
    @ApiModelProperty("配置描述")
    @Length(max = 1024, message = "配置描述最长为1024")
    private String desc;

    /**
     * 配置作用范围 0：探针配置，1：agent配置
     */
    @ApiModelProperty("配置生效范围0：探针配置，1：agent配置，默认是0")
    private Integer effectType;

    /**
     * 生效机制0：重启生效，1：立即生效
     */
    @ApiModelProperty("生效机制0：重启生效，1：立即生效，默认是0")
    private Integer effectMechanism;

    /**
     * 配置生效最低版本
     */
    @ApiModelProperty(value = "配置生效最低版本", hidden = true)
    private String effectMinVersion;

    /**
     * 配置生效最大版本 废弃
     */
    @ApiModelProperty(value = "配置生效最大版本", hidden = true)
    private String effectMaxVersion;

    /**
     * 是否可编辑0：可编辑，1：不可编辑
     */
    @ApiModelProperty("是否可编辑0：可编辑，1：不可编辑，默认为0")
    private Integer editable;

    /**
     * 值类型0：文本，1：单选
     */
    @ApiModelProperty("值类型0：文本，1：单选，默认是0")
    private Integer valueType;

    /**
     * 值类型为单选时的可选项
     */
    @ApiModelProperty("值类型为单选时的可选项")
    private List<String> valueOptionList;

    /**
     * 应用名称（应用配置时才生效）
     */
    @ApiModelProperty("应用名称（应用配置时才生效）")
    private String projectName;

}
