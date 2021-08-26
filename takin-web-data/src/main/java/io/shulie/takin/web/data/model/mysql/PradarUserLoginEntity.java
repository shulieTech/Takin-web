package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户登录表
 */
@Data
@TableName(value = "pradar_user_login")
public class PradarUserLoginEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "password")
    private String password;

    @TableField(value = "dept")
    private String dept;

    /**
     * 1：测试， 2：开发， 3：运维， 4：管理
     */
    @TableField(value = "user_type")
    private Boolean userType;

    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    @TableField(value = "gmt_created")
    private LocalDateTime gmtCreated;

    @TableField(value = "gmt_modified")
    private LocalDateTime gmtModified;
}
