package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 下午8:07
 */
@Data
@TableName(value = "t_leakverify_result")
public class LeakVerifyResultEntity extends UserBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 引用类型 0:压测场景;1:业务流程;2:业务活动
     */
    @TableField(value = "ref_type")
    private Integer refType;

    /**
     * 引用id
     */
    @TableField(value = "ref_id")
    private Long refId;

    /**
     * 报告id
     */
    @TableField(value = "report_id")
    private Long reportId;

    /**
     * 数据源id
     */
    @TableField(value = "dbresource_id")
    private Long dbresourceId;

    /**
     * 数据源名称
     */
    @TableField(value = "dbresource_name")
    private String dbresourceName;

    /**
     * 数据源地址
     */
    @TableField(value = "dbresource_url")
    private String dbresourceUrl;

//    /**
//     * 是否漏数 0:正常;1:漏数;2:未检测;3:检测失败
//     */
//    @TableField(value = "status")
//    private Integer status;

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

    public static final String COL_SCENE_ID = "scene_id";

    public static final String COL_REPORT_ID = "report_id";

    public static final String COL_DBRESOURCE_ID = "dbresource_id";

    public static final String COL_DBRESOURCE_NAME = "dbresource_name";

    public static final String COL_DBRESOURCE_URL = "dbresource_url";

    public static final String COL_IS_LEAKED = "is_leaked";


    public static final String COL_USER_ID = "user_id";

    public static final String COL_REMARK = "remark";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
