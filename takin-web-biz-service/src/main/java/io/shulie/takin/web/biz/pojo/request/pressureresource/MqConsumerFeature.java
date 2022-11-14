package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/11 11:16 AM
 */
@Data
public class MqConsumerFeature {
    @ApiModelProperty("集群名称")
    private String clusterName;

    @ApiModelProperty("集群地址")
    private String clusterAddr;

    @ApiModelProperty("生产或消费的线程数")
    private Integer providerThreadCount;

    @ApiModelProperty("生产或消费的线程数")
    private Integer consumerThreadCount;
}
