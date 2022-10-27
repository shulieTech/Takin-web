package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class PressureResourceMqConsumerCreateInput {
    @ApiModelProperty(value = "Id")
    private Long id;

    /**
     * topic
     */
    @ApiModelProperty(value = "topic")
    private String topic;

    /**
     * topic
     */
    @ApiModelProperty(value = "group")
    private String group;

    @ApiModelProperty(value = "resourceId")
    private Long resourceId;

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

    @ApiModelProperty(value = "消费者类型RABBITMQ/ROCKETMQ/KAFKA/KAFKA-其他")
    private String mqType;

    private Long applicationId;

    /**
     * 是否消费(0-消费 1-不消费)
     */
    private Integer consumerTag;

    @ApiModelProperty("是否影子集群(0-是 1否)")
    private Integer isCluster;

    @ApiModelProperty("生产或消费(0-生产,1-消费)")
    private Integer comsumerType;

    /**
     * 来源标识
     */
    private Integer type;

    @ApiModelProperty("kafka的时候的集群信息")
    private MqConsumerFeature mqConsumerFeature;

    @ApiModelProperty("批量Id")
    private List<Long> ids;
}
