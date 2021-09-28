package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/4 11:08 上午
 */
@Data
@TableName(value = "t_performance_thread_stack_data")
public class PerformanceThreadStackDataEntity extends TenantBaseEntity {

    @TableField(value = "thread_stack")
    private String threadStack;

    @TableField(value = "thread_stack_link")
    private Long threadStackLink;

    @TableField(value = "gmt_create")
    private Date gmtCreate;
}
