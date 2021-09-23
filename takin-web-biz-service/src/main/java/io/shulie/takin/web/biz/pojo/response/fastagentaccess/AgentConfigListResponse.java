package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/16 10:50 上午
 */
@Data
public class AgentConfigListResponse {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 配置类型0：全局配置，1：应用配置
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigTypeEnum
     */
    @ApiModelProperty("0：全局配置，1：应用配置")
    private Integer type;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date gmtUpdate;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date gmtCreate;

    /**
     * 中文配置key
     */
    @ApiModelProperty("中文配置key")
    private String zhKey;

    /**
     * 英文配置key
     */
    @ApiModelProperty("英文配置key")
    private String enKey;

    /**
     * 配置生效最低版本
     */
    @ApiModelProperty("配置生效最低版本")
    private String effectMinVersion;

    /**
     * 配置描述
     */
    @ApiModelProperty("配置描述")
    private String desc;

    /**
     * 值类型0：文本，1：单选
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigValueTypeEnum
     */
    @ApiModelProperty("值类型0：文本，1：单选")
    private Integer valueType;

    /**
     * 配置默认值
     */
    @ApiModelProperty("配置值")
    private String defaultValue;

    /**
     * 生效机制0：重启生效，1：立即生效
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum
     */
    @ApiModelProperty("生效机制0：重启生效，1：立即生效")
    private Integer effectMechanism;

    /**
     * 是否可编辑0：可编辑，1：不可编辑
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigEditableEnum
     */
    @ApiModelProperty("是否可编辑0：可编辑，1：不可编辑")
    private Integer editable;

    /**
     * 是否生效
     */
    @ApiModelProperty("是否生效，true:生效，false:不生效")
    private Boolean isEffect;
}
