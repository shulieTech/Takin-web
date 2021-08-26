package io.shulie.takin.web.data.result.application;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuchuan
 * @date 2021/7/8 11:56 上午
 */
@Getter
@Setter
public class ApplicationMiddlewareStatusAboutCountResult {

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 统计
     */
    private Integer count;

}
