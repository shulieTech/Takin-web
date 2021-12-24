package io.shulie.takin.web.common.pojo.dto.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * mq消息体, 通用类
 *
 * @author liuchuan
 * @date 2021/12/17 3:13 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class MqApplicationMiddlewareCompareDTO extends MqBodyCommonDTO {

    /**
     * 应用id
     */
    private Long applicationId;

}
