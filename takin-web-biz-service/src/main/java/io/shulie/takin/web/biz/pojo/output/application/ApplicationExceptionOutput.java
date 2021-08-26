package io.shulie.takin.web.biz.pojo.output.application;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/1/7 11:52 上午
 */
@Data
public class ApplicationExceptionOutput {
    /**
     * 应用名
     */
    private String applicationName;

    /**
     * agentId
     */
    private List<String> agentIds;
    /**
     * 异常类型
     */
    private String type;

    /**
     * 异常编码
     */
    private String code;

    /**
     * 异常描述
     */
    private String description;

    /**
     * 异常时间
     */
    private String time;
}
