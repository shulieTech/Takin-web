package io.shulie.takin.web.amdb.bean.result.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 南风
 * @Date: 2021/8/25 5:20 下午
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ApplicationRemoteCallTypeTemplateDTO {

    /**
     * 中间件英文名称
     */
    private String engName;

    /**
     * 是否支持自动梳理;0:不支持;1:支持
     */
    private Integer cobmEnable;

    /**
     * 是否支持白名单;0:不支持;1:支持
     */
    private Integer whitelistEnable;

    /**
     * 是否支持返回值mock;0:不支持;1:支持
     */
    private Integer returnMockEnable;

    /**
     * 返回值mock文本
     */
    private String returnMock;

    /**
     * 是否支持转发mock;0:不支持;1:支持
     */
    private Integer forwardMockEnable;

    /**
     * 转发mock文本
     */
    private String forwardMock;
}
