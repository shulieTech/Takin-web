package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 中间件信息表
    */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@TableName(value = "t_middleware_summary")
public class MiddlewareSummaryEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 中间件中文名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty("中间件中文名称")
    private String name;

    /**
     * 中间件类型
     */
    @TableField(value = "`type`")
    @ApiModelProperty("中间件类型")
    private String type;

    /**
     * 中间件总数
     */
    @TableField(value = "total_num")
    @ApiModelProperty("中间件总数")
    private Long totalNum;

    /**
     * 已支持数量
     */
    @TableField(value = "supported_num")
    @ApiModelProperty("已支持数量")
    private Long supportedNum;

    /**
     * 未知数量
     */
    @TableField(value = "unknown_num")
    @ApiModelProperty("未知数量")
    private Long unknownNum;

    /**
     * 未支持的数量
     */
    @TableField(value = "not_supported_num")
    @ApiModelProperty("未支持的数量")
    private Long notSupportedNum;

    /**
     * 中间件名称
     */
    @TableField(value = "artifact_id")
    @ApiModelProperty("artifactId")
    private String artifactId;

    /**
     * 中间件组织名称
     */
    @TableField(value = "group_id")
    @ApiModelProperty("groupId")
    private String groupId;

    /**
     * artifactId_groupId, 做唯一标识,
     */
    @TableField(value = "ag")
    @ApiModelProperty("artifactId_groupId, 做唯一标识,")
    private String ag;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    @ApiModelProperty("中间件类型")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_update")
    @ApiModelProperty("更新时间")
    private Date gmtUpdate;

    /**
     * 备注
     */
    @TableField(value = "`commit`")
    @ApiModelProperty("备注")
    private String commit;

    @TableField(value = "`status`")
    private Integer status;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_TYPE = "type";

    public static final String COL_TOTAL_NUM = "total_num";

    public static final String COL_SUPPORTED_NUM = "supported_num";

    public static final String COL_UNKNOWN_NUM = "unknown_num";

    public static final String COL_NOT_SUPPORTED_NUM = "not_supported_num";

    public static final String COL_ARTIFACT_ID = "artifact_id";

    public static final String COL_GROUP_ID = "group_id";

    public static final String COL_AG = "ag";

    public static final String COL_GMT_CREATE = "gmt_create";

    public static final String COL_GMT_UPDATE = "gmt_update";

    public static final String COL_IS_DELETED = "is_deleted";

    public static final String COL_COMMIT = "commit";

    public static final String COL_STATUS = "status";
}