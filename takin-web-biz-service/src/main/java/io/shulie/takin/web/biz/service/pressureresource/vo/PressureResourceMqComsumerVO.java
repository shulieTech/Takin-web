package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.shulie.takin.web.biz.pojo.request.pressureresource.MqConsumerFeature;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceMqComsumerVO {
    /**
     * 主键id
     */
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("资源配置Id")
    private Long resourceId;

    /**
     * topic
     */
    private String topic;

    /**
     * topic
     */
    private String group;

    /**
     * broker地址
     */
    private String brokerAddr;

    /**
     * topicTokens
     */
    private String topicTokens;

    /**
     * systemIdToken
     */
    private String systemIdToken;

    /**
     * MQ类型
     */
    private String mqType;

    /**
     * 应用id
     */
    private Long applicationId;

    private String applicationName;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    private Integer status;

    @ApiModelProperty("是否消费(0-消费 1-不消费 )")
    private Integer consumerTag;

    /**
     * 是否删除，0正常，1删除
     */
    private Integer deleted;

    /**
     * 拓展字段
     */
    private String feature;

    @ApiModelProperty("remark")
    private String remark;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    private Integer type;

    @ApiModelProperty("是否影子集群(0-是 1否)")
    private Integer isCluster;

    @ApiModelProperty("生产或消费(0-生产,1-消费)")
    private Integer comsumerType;

    private MqConsumerFeature mqConsumerFeature;
}
