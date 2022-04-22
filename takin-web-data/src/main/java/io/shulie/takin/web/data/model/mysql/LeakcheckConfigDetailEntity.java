package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 下午2:22
 */
@Data
@TableName(value = "t_leakcheck_config_detail")
@EnableSign
public class LeakcheckConfigDetailEntity extends UserBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据源id
     */
    @TableField(value = "datasource_id")
    private Long datasourceId;

    public static final String COL_SQL_CONTENT = "sql_content";
    /**
     * 漏数sql
     */
    @TableField(value = "sql_content")
    private String sqlContent;


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

    public static final String COL_ID = "id";

    public static final String COL_DATASOURCE_ID = "datasource_id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_REMARK = "remark";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";

    @TableField(value = "sign",fill = FieldFill.INSERT)
    private String sign;
}
