package io.shulie.takin.web.data.model.mysql;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_pressure_machine")
public class PressureMachineEntity extends TenantBaseEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压力机名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 压力机IP
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 标签
     */
    @TableField(value = "flag")
    private String flag;

    /**
     * cpu核数
     */
    @TableField(value = "cpu")
    private Integer cpu;

    /**
     * 内存，单位字节
     */
    @TableField(value = "memory")
    private Long memory;


    @TableField(value = "machine_usage")
    private BigDecimal machineUsage;
    /**
     * 磁盘，单位字节
     */
    @TableField(value = "disk")
    private Long disk;

    /**
     * cpu利用率
     */
    @TableField(value = "cpu_usage")
    private BigDecimal cpuUsage;

    /**
     * cpu load
     */
    @TableField(value = "cpu_load")
    private BigDecimal cpuLoad;

    /**
     * 内存利用率
     */
    @TableField(value = "memory_used")
    private BigDecimal memoryUsed;

    /**
     * 磁盘 IO 等待率
     */
    @TableField(value = "disk_io_wait")
    private BigDecimal diskIoWait;

    /**
     * 网络带宽总大小
     */
    @TableField(value = "transmitted_total")
    private Long transmittedTotal;
    /**
     * 网络带宽入大小
     */
    @TableField(value = "transmitted_in")
    private Long transmittedIn;

    /**
     * 网络带宽入利用率
     */
    @TableField(value = "transmitted_in_usage")
    private BigDecimal transmittedInUsage;

    /**
     * 网络带宽出大小
     */
    @TableField(value = "transmitted_out")
    private Long transmittedOut;

    /**
     * 网络带宽出利用率
     */
    @TableField(value = "transmitted_out_usage")
    private BigDecimal transmittedOutUsage;

    /**
     * 网络带宽利用率
     */
    @TableField(value = "transmitted_usage")
    private BigDecimal transmittedUsage;

    /**
     * 压测场景id
     */
    @TableField(value = "scene_names")
    private String sceneNames;

    /**
     * 状态 0：空闲 ；1：压测中  -1:离线
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 状态 0: 正常 1： 删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private String gmtCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gmt_update")
    private String gmtUpdate;
}
