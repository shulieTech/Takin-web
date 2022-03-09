package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * agent配置管理(AgentConfig)实体类
 *
 * @author liuchuan
 * @date 2021-08-12 18:56:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_agent_config")
@ToString(callSuper = true)
@EnableSign
public class AgentConfigEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 339110573700928675L;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 配置类型0：全局配置，1：应用配置
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigTypeEnum
     */
    @SignField(order = 1)
    private Integer type;

    /**
     * 中文配置key
     */
    @SignField(order = 2)
    private String zhKey;

    /**
     * 英文配置key
     */
    @SignField(order = 3)
    private String enKey;

    /**
     * 配置默认值
     */
    @SignField(order = 4)
    private String defaultValue;

    /**
     * 配置描述
     */
    @SignField(order = 5)
    @TableField("`desc`")
    private String desc;

    /**
     * 配置作用范围 0：探针配置，1：agent配置
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigEffectTypeEnum
     */
    @SignField(order = 6)
    private Integer effectType;

    /**
     * 生效机制0：重启生效，1：立即生效
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum
     */
    @SignField(order = 7)
    private Integer effectMechanism;

    /**
     * 配置生效最低版本
     */
    @SignField(order = 8)
    private String effectMinVersion;

    /**
     * 配置生效最低版本对应的数值
     */
    @SignField(order = 9)
    private Long effectMinVersionNum;

    /**
     * 配置生效最大版本
     */
    private String effectMaxVersion;

    /**
     * 配置生效最大版本对应的数值
     */
    private Long effectMaxVersionNum;

    /**
     * 是否可编辑0：可编辑，1：不可编辑
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigEditableEnum
     */
    @SignField(order = 10)
    private Integer editable;

    /**
     * 值类型0：文本，1：单选
     *
     * @see io.shulie.tro.web.common.enums.fastagentaccess.AgentConfigValueTypeEnum
     */
    @SignField(order = 11)
    private Integer valueType;

    /**
     * 值类型为单选时的可选项，多个可选项之间用,分隔
     */
    private String valueOption;

    /**
     * 应用名称（应用配置时才生效）
     */
    private String projectName;

    /**
     * 用户信息（应用配置时才生效）
     */
    private String userAppKey;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    private Integer isDeleted;

    @TableField(value = "`desc`",fill = FieldFill.INSERT)
    private String sign;

}
