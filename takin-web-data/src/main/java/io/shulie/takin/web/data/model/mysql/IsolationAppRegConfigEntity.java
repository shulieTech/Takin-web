package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_isolation_app_reg_config")
public class IsolationAppRegConfigEntity {
    /**
     * 主键ID
     */
    @TableId(value = "reg_id", type = IdType.AUTO)
    private Long regId;

    /**
     * 注册中心类型
     */
    @TableField(value = "reg_type")
    private String regType;

    /**
     * 注册中心地址
     */
    @TableField(value = "reg_addr")
    private String regAddr;

    /**
     * 是否启用
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
