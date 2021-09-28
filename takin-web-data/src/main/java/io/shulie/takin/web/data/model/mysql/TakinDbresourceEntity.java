package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_tro_dbresource")
public class TakinDbresourceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0:MYSQL
     */
    @TableField(value = "type")
    private Integer type;

    public static final String COL_NAME = "name";

    /**
     * 数据源地址
     */
    @TableField(value = "jdbc_url")
    private String jdbcUrl;

    /**
     * 数据源用户
     */
    @TableField(value = "username")
    private String username;

    /**
     * 数据源密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 扩展字段，k-v形式存在
     */
    @TableField(value = "features")
    private String features;
    /**
     * 数据源名称
     */
    @TableField(value = "name")
    private String name;


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

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    public static final String COL_ID = "id";

    public static final String COL_TYPE = "type";
    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    public static final String COL_JDBC_URL = "jdbc_url";

    public static final String COL_USERNAME = "username";

    public static final String COL_PASSWORD = "password";

    public static final String COL_FEATURES = "features";

    public static final String COL_CUSTOMER_ID = "customer_id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_REMARK = "remark";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
