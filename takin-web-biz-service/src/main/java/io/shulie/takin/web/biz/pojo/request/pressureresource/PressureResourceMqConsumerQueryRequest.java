package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XINGCHEN
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
public class PressureResourceMqConsumerQueryRequest extends PageBaseDTO {
    @ApiModelProperty("消费组")
    private String queryTopicGroup;

    @ApiModelProperty("resourceId")
    private Long resourceId;

    @ApiModelProperty("queryApplicationName")
    private String queryApplicationName;

    @ApiModelProperty("mqType")
    private String mqType;

    @ApiModelProperty("consumerTag")
    private Integer consumerTag;
}
