package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * db连接池影子库表配置表(ApplicationDsDbManage)实体类
 *
 * @author 南风
 * @date 2021-08-30 14:41:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_ds_db_manage")
@ToString(callSuper = true)
@EnableSign
public class ApplicationDsDbManageEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -95402226923523212L;

    /**
     * 应用主键
     */
    private Long applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 连接池名称 druid, hikari,c3p0等
     */
    private String connPoolName;

    /**
     * 数据源名称
     */
    private String dbName;

    /**
     * 业务数据源url
     */
    private String url;

    /**
     * 源用户名
     */
    private String userName;

    /**
     * 源密码
     */
    private String pwd;

    /**
     * 方案类型 0:影子库 1:影子表 2:影子server
     */
    private Integer dsType;

    /**
     * 影子数据源url
     */
    private String shaDowUrl;

    /**
     * 影子数据源用户名
     */
    private String shaDowUserName;

    /**
     * 影子数据源密码
     */
    private String shaDowPwd;

    /**
     * 连接池额外配置,存json
     */
    private String fileExtedn;

    /**
     * 影子方案额外配置
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
     * 用户id
     */
    @TableField(value = "user_id" , fill = FieldFill.INSERT)
    private Long userId;

    private String agentSourceType;

    @TableField(value = "sign" , fill = FieldFill.INSERT)
    private String sign;

    private Long customerId;

}
