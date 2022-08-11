package io.shulie.takin.web.data.model.mysql;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_machine_manage")
public class MachineManageEntity extends TenantBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "machine_name")
    private String machineName;

    @TableField(value = "machine_ip")
    private String machineIp;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "password")
    private String password;

    @TableField(value = "remark")
    private String remark;

    @TableField(value = "benchmark_suite_name")
    private String benchmarkSuiteName;

    /**
     * 部署类型
     */
    @TableField(value = "deploy_type")
    private String deployType;

    /**
     * '状态 0：未部署 ；1：部署中  2:已部署'
     */
    @TableField(value = "status")
    private Integer status;

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
     * 添加逻辑删除注解
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;
}
