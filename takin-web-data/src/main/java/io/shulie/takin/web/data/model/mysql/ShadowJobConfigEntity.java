package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 影子JOB任务配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_shadow_job_config")
@EnableSign
public class ShadowJobConfigEntity extends UserBaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 应用ID
     */
    @TableField(value = "application_id")
    @SignField(order = 1)
    private Long applicationId;

    /**
     * 任务名称
     */
    @TableField(value = "name")
    @SignField(order = 2)
    private String name;

    /**
     * JOB类型 0-quartz、1-elastic-job、2-xxl-job
     */
    @TableField(value = "type")
    @SignField(order = 3)
    private Integer type;

    /**
     * 配置代码
     */
    @TableField(value = "config_code")
    @SignField(order = 4)
    private String configCode;

    /**
     * 0-可用 1-不可用
     */
    @TableField(value = "status")
    @SignField(order = 5)
    private Integer status;

    /**
     * 0-可用 1-不可用
     */
    @TableField(value = "active")
    @SignField(order = 6)
    private Integer active;


    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    @TableField(value = "sign", fill = FieldFill.INSERT)
    private String sign;

}
