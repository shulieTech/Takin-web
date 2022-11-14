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
    @ApiModelProperty("topic")
    private String topic;

    @ApiModelProperty("group")
    private String group;

    @ApiModelProperty("资源配置ID")
    private Long resourceId;

    @ApiModelProperty("mqType")
    private String mqType;

    @ApiModelProperty("模糊查询，消费组")
    private String queryTopicGroup;

    @ApiModelProperty("queryApplicationName")
    private String queryApplicationName;

    @ApiModelProperty("consumerTag")
    private Integer consumerTag;
}
