package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 下午8:07
 */
@Data
@TableName(value = "t_leakverify_detail")
public class LeakVerifyDetailEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 验证结果id
     */
    @TableField(value = "result_id")
    private Long resultId;

    /**
     * 漏数sql
     */
    @TableField(value = "leak_sql")
    private String leakSql;

    /**
     * 是否漏数 0:正常;1:漏数;2:未检测;3:检测失败
     */
    @TableField(value = "status")
    private Integer status;

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
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

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
    public static final String COL_ID = "id";

    public static final String COL_RESULT_ID = "result_id";

    public static final String COL_LEAK_SQL = "leak_sql";

    public static final String COL_IS_LEAKED = "is_leaked";

    public static final String COL_REMARK = "remark";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
