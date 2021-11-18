package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 缓存配置模版表(CacheConfigTemplate)实体类
 *
 * @author 南风
 * @date 2021-08-30 10:28:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_cache_config_template")
@ToString(callSuper = true)
public class CacheConfigTemplateEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 803735316845518786L;

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
     * 是否支持影子key;0:不支持;1:支持
     */
    private Integer shadowtdbEnable;

    /**
     * 是否支持影子集群;0:不支持;1:支持
     */
    private Integer shadowttableEnable;

    /**
     * 配置文本
     */
    private String config;

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
