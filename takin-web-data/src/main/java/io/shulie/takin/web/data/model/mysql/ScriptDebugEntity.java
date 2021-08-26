package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 脚本调试表(ScriptDebug)实体类
 *
 * @author liuchuan
 * @date 2021-05-10 16:55:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_script_debug")
public class ScriptDebugEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -11150233689819474L;

    /**
     * 脚本发布id
     */
    private Long scriptDeployId;

    /**
     * 调试记录状态, 0 未启动(默认), 1 启动中, 2 请求中, 3 请求结束, 4 调试成功, 5 调试失败
     */
    private Integer status;

    /**
     * 失败类型, 10 启动通知超时失败, 20 漏数失败, 30 非200检查失败, 后面会扩展
     */
    private Integer failedType;

    /**
     * 检查漏数状态, 0:正常;1:漏数;2:未检测;3:检测失败, 99 未配置漏数脚本
     */
    private Integer leakStatus;

    /**
     * 备注, 当调试失败时, 有失败信息
     */
    private String remark;

    /**
     * 请求条数
     */
    private Integer requestNum;

    /**
     * 并发数
     */
    @TableField(value = "concurrency_num")
    private Integer concurrencyNum;


    /**
     * 对应的 cloud 场景id
     */
    private Long cloudSceneId;

    /**
     * 对应的 cloud 报告id
     */
    private Long cloudReportId;

    /**
     * 租户id
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 租户下的用户id
     */
    @TableField(value = "user_id", fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
