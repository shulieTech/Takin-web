package io.shulie.takin.web.data.model.mysql.pressureresource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 影子消费者
 */
@Data
@TableName(value = "t_pressure_resource_relate_shadow_mq_consumer")
@EnableSign
public class PressureResourceRelateMqConsumerEntity extends TenantBaseEntity {
    /**
     * 主键id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @TableField(value = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("详情Id")
    @TableField(value = "`detail_id`")
    private Long detailId;

    /**
     * topic
     */
    @TableField(value = "`topic`")
    private String topic;

    /**
     * topic
     */
    @TableField(value = "`group`")
    private String group;

    /**
     * broker地址
     */
    @TableField(value = "`broker_addr`")
    private String brokerAddr;

    /**
     * topicTokens
     */
    @TableField(value = "`topic_tokens`")
    private String topicTokens;

    /**
     * systemIdToken
     */
    @TableField(value = "`systemId_Token`")
    private String systemIdToken;

    /**
     * MQ类型
     */
    @TableField(value = "`mq_type`")
    private String mqType;

    /**
     * 应用id
     */
    @TableField(value = "`application_id`")
    private Long applicationId;
    /**
     * 应用id
     */
    @TableField(value = "`application_name`")
    private String applicationName;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    @TableField(value = "`status`")
    private Integer status;

    @ApiModelProperty("是否消费(0-消费 1-不消费 )")
    @TableField(value = "`consumer_tag`")
    private Integer consumerTag;

    /**
     * 是否删除，0正常，1删除
     */
    @TableField(value = "`deleted`")
    private Integer deleted;

    /**
     * 拓展字段
     */
    @TableField(value = "`feature`")
    private String feature;

    @ApiModelProperty("remark")
    @TableField(value = "`remark`")
    private String remark;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    @TableField(value = "`type`")
    private Integer type;

    @ApiModelProperty("是否影子集群(0-是 1否)")
    @TableField(value = "`is_cluster`")
    private Integer isCluster;

    @ApiModelProperty("生产或消费(0-生产,1-消费)")
    @TableField(value = "`comsumer_type`")
    private Integer comsumerType;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_modified")
    private Date gmtModified;
}
