package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "pradar_app_point")
public class PradarAppPointEntity extends TenantBaseEntity {
    /**
     * 埋点ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用ID
     */
    @TableField(value = "app_info_id")
    private Long appInfoId;

    /**
     * 埋点方法
     */
    @TableField(value = "method")
    private String method;

    /**
     * 删除标识
     */
    @TableField(value = "method_comment")
    private String methodComment;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField(value = "modify_time")
    private LocalDateTime modifyTime;

    /**
     * 删除标识
     */
    @TableField(value = "deleted")
    private Integer deleted;
}
