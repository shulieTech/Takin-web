package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 缓存影子库表配置表(ApplicationDsCacheManage)实体类
 *
 * @author 南风
 * @date 2021-08-30 14:40:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_ds_cache_manage")
@ToString(callSuper = true)
public class ApplicationDsCacheManageEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 214783401261180458L;

    /**
     * 应用主键
     */
    private Long applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 中间件名称
     */
    private String cacheName;

    /**
     * 集群地址
     */
    private String colony;

    /**
     * 源用户名
     */
    private String userName;

    /**
     * 源密码
     */
    private String pwd;


    private String type;

    /**
     * 方案类型 6:影子key 7:影子集群
     */
    private Integer dsType;

    /**
     * 额外配置,存json
     */
    private String fileExtedn;

    /**
     * 影子额外配置
     */
    private String shaDowFileExtedn;

    /**
     * 配置全部参数json化
     */
    private String configJson;

    /**
     * 方案类型 0:amdb 1:手动录入
     */
    private Integer source;

    /**
     * 状态 0:启用；1:禁用
     */
    private Integer status;

    /**
     * 租户id
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 用户id
     */
    @TableField(value = "user_id" , fill = FieldFill.INSERT)
    private Long userId;

    private String agentSourceType;

}
