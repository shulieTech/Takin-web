package com.pamirs.takin.entity.domain.entity.report;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_report_task")
public class ReportTaskEntity implements Serializable {

    @TableId
    private Long id;

    @TableField(value = "report_id")
    private Long reportId;

    /**
     * 0-分析中 1- 同步中 2-同步完成
     */
    @TableField(value = "state")
    private Integer state;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gmt_update")
    private Date gmtUpdate;
}
