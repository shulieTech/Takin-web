package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
    * 中间件包表
    */
@Data
@TableName(value = "t_middleware_jar")
public class MiddlewareJarEntity{
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 中间件中文名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 中间件类型
     */
    @TableField(value = "`type`")
    private String type;

    /**
     * 支持的包状态, 1 已支持, 2 待支持, 3 无需支持, 4 待验证
     */
    @TableField(value = "`status`")
    private Integer status;

    /**
     * 中间件名称
     */
    @TableField(value = "artifact_id")
    private String artifactId;

    /**
     * 中间件组织名称
     */
    @TableField(value = "group_id")
    private String groupId;

    /**
     * 中间件版本
     */
    @TableField(value = "version")
    private String version;

    /**
     * artifactId_groupId_version, 做唯一标识,
     */
    @TableField(value = "agv")
    private String agv;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    @TableField(value = "remark")
    private String remark;

    @TableField(value = "`commit`")
    private String commit;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_TYPE = "type";

    public static final String COL_STATUS = "status";

    public static final String COL_ARTIFACT_ID = "artifact_id";

    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_VERSION = "version";

    public static final String COL_AGV = "agv";

    public static final String COL_GMT_CREATE = "gmt_create";

    public static final String COL_GMT_UPDATE = "gmt_update";

    public static final String COL_IS_DELETED = "is_deleted";

    public static final String COL_REMARK = "remark";

    public static final String COL_COMMIT = "commit";
}