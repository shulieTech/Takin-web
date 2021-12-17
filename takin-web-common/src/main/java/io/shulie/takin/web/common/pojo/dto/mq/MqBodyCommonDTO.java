package io.shulie.takin.web.common.pojo.dto.mq;

import lombok.Data;

/**
 * mq消息体, 通用类
 *
 * @author liuchuan
 * @date 2021/12/17 3:13 下午
 */
@Data
public class MqBodyCommonDTO {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 环境
     */
    private String envCode;

}
