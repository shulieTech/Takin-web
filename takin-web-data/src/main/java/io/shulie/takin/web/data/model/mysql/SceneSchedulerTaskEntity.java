package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 21:17
 */

@Data
@TableName(value = "t_scene_scheduler_task")
public class SceneSchedulerTaskEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "scene_id")
    private Long sceneId ;
    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
    @TableField(value = "user_id" , fill = FieldFill.INSERT)
    private Long userId ;

    @TableField(value = "content")
    private String content ;

    /**
     * 0：待执行，1:执行中；2:已执行
     */
    @TableField(value = "is_executed")
    private Integer isExecuted ;

    @TableField(value = "execute_time")
    private Date executeTime ;

    @TableField(value = "is_deleted")
    private Boolean isDeleted ;

    @TableField(value = "gmt_create")
    private Date gmtCreate ;

    @TableField(value = "gmt_update")
    private Date gmtUpdate ;


}
