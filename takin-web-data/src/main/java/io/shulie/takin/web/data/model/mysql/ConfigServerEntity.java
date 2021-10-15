package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.CommonWithTenantKeyEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 配置表-服务的配置(ConfigServer)实体类
 *
 * @author liuchuan
 * @date 2021-10-12 11:17:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_config_server")
@ToString(callSuper = true)
public class ConfigServerEntity extends CommonWithTenantKeyEntity implements Serializable {
    private static final long serialVersionUID = 175251610571313392L;

    /**
     * 配置的 key
     */
    @TableField("`key`")
    private String key;

    /**
     * 配置的值
     */
    @TableField("`value`")
    private String value;

    /**
     * 是否是租户使用, 1 是, 0 否
     */
    private Integer isTenant;

    /**
     * 是否是全局的, 1 是, 0 否
     */
    private Integer isGlobal;

    /**
     * 归属版本, 1 开源版, 2 企业版, 6 开源版和企业版
     */
    private Integer edition;

}
