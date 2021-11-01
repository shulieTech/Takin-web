package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * MQ配置模版表(MqConfigTemplate)实体类
 *
 * @author 南风
 * @date 2021-08-31 15:34:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_mq_config_template")
@ToString(callSuper = true)
public class MqConfigTemplateEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -52813831963978645L;

    /**
     * 中间件中文描述
     */
    private String name;

    /**
     * 中间件所属类型
     */
    private String type;

    /**
     * 中间件英文名称
     */
    private String engName;

    /**
     * 是否支持自动梳理;0:不支持;1:支持
     */
    private Integer cobmEnable;

    /**
     * 是否支持影子消费;0:不支持;1:支持
     */
    private Integer shadowconsumerEnable;

    /**
     * 1.可用，2不可用
     */
    private Integer status;

    /**
     * 标记
     */
    private String remark;

    /**
     * 备注
     */
    private String commit;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;


}
