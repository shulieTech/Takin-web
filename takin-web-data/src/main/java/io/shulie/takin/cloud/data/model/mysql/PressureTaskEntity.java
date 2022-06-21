package io.shulie.takin.cloud.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_pressure_task")
public class PressureTaskEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源Id
     */
    @TableField("resource_id")
    private String resourceId;

    /**
     * 压测引擎任务Id
     */
    @TableField("job_id")
    private Long jobId;

    @TableField("report_id")
    private Long reportId;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 场景名称
     */
    @TableField(value = "scene_name")
    private String sceneName;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    /**
     * 用户主键
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 租户主键
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 用户id
     */
    @TableField(value = "env_code")
    private String envCode;

    /**
     * 是否删除:0/正常，1、已删除
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 操作用户ID
     */
    @TableField(value = "operate_id")
    private Long operateId;

    /**
     * 异常信息
     */
    @TableField(value = "exception_msg")
    private String exceptionMsg;
}
