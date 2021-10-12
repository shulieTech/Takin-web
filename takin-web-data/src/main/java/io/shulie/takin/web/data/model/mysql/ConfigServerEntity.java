package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
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
public class ConfigServerEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 175251610571313392L;

    /**
     * 配置的 key
     */
    private String key;

    /**
     * 配置的值
     */
    private String value;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 是否租户使用
     */
    private Integer isTenant;

    /**
     * 是否展示
     */
    private Integer isShow;

    /**
     * 归属版本, 1 开源版, 2 企业版, 6 开源版和企业版
     */
    private Integer edition;

    /**
     * 环境
     */
    private String envCode;

    /**
     * 租户key
     */
    private String userAppKey;

}
