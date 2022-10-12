package io.shulie.takin.web.data.param.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PressureResourceMqConsumerQueryParam extends PageBaseDTO {
    @ApiModelProperty("topicGroup")
    private String topicGroup;

    @ApiModelProperty("资源配置ID")
    private Long resourceId;

    @ApiModelProperty("mqType")
    private String mqType;

    @ApiModelProperty("模糊查询，消费组")
    private String queryTopicGroup;

    @ApiModelProperty("applicationName")
    private String applicationName;

    @ApiModelProperty("consumerTag")
    private Integer consumerTag;
}
