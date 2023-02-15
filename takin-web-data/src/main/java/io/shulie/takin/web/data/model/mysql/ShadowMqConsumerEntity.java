package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
 * 影子消费者
 */
@Data
@TableName(value = "t_shadow_mq_consumer")
@EnableSign
public class ShadowMqConsumerEntity extends UserBaseEntity {
    public static final String COL_TOPIC_GROUP = "topic_group";
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * topic
     */
    @TableField(value = "topic_group")
    private String topicGroup;

    /**
     * 自定义影子消费者topic和group
     */
    @TableField(value = "customize_topic_group")
    private String customizeTopicGroup;

    /**
     * MQ类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 应用id
     */
    @TableField(value = "application_id")
    private Long applicationId;

    /**
     * 应用名称，冗余
     */
    @TableField(value = "application_name")
    private String applicationName;

    /**
     * 是否可用(0表示未启用,1表示已启用)
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否删除，0正常，1删除
     */
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 拓展字段
     */
    @TableField(value = "feature")
    private String feature;

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
     * 是否手动录入 0否，1手动
     */
    @TableField(value = "manual_tag")
    private Integer manualTag;

    @TableField(value = "sign", fill = FieldFill.INSERT)
    private String sign;


    public static final String COL_ID = "id";

    public static final String COL_TOPIC = "topic";

    public static final String COL_TYPE = "type";

    public static final String COL_GROUP = "group";

    public static final String COL_APPLICATION_ID = "application_id";

    public static final String COL_APPLICATION_NAME = "application_name";

    public static final String COL_STATUS = "status";


    public static final String COL_USER_ID = "user_id";

    public static final String COL_DELETED = "deleted";

    public static final String COL_FEATURE = "feature";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}
