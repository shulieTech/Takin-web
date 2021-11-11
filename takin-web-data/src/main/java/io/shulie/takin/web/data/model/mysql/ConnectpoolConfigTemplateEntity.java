package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.CommonWithUserIdAndTenantIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 连接池配置模版表(ConnectpoolConfigTemplate)实体类
 *
 * @author 南风
 * @date 2021-08-30 10:31:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_connectpool_config_template")
@ToString(callSuper = true)
public class ConnectpoolConfigTemplateEntity extends CommonWithUserIdAndTenantIdEntity implements Serializable {
    private static final long serialVersionUID = -23110565824286312L;

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
     * 是否支持影子表方案;0:不支持;1:支持
     */
    private Integer shadowtableEnable;

    /**
     * 是否支持影子库方案;0:不支持;1:支持
     */
    private Integer shadowdbEnable;

    /**
     * 是否支持影子库和影子库方案;0:不支持;1:支持
     */
    private Integer shadowdbwithshadowtableEnable;

    /**
     * 影子库方案配置属性
     */
    private String shadowdbAttribute;

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



}
