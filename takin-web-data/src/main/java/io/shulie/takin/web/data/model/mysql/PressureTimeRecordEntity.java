package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测时间记录表
 */
@Data
@TableName(value = "t_pressure_time_record")
public class PressureTimeRecordEntity {
    /**
     * 压测记录id
     */
    @TableId(value = "RECORD_ID", type = IdType.INPUT)
    private String recordId;

    /**
     * 开始压测时间
     */
    @TableField(value = "START_TIME")
    private String startTime;

    /**
     * 结束压测时间
     */
    @TableField(value = "END_TIME")
    private String endTime;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
