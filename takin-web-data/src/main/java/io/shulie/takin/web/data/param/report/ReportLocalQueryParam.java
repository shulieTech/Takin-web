package io.shulie.takin.web.data.param.report;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.common.beans.page.PagingDevice;

/**
 * @author qianshui
 * @date 2020/7/27 下午8:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportLocalQueryParam extends PagingDevice implements Serializable {

    /**
     * 报告id
     */
    private Long reportId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 机器ip
     */
    private String machineIp;

    /**
     * 是否风险机器
     */
    private Integer riskFlag;

    /**
     * agentId
     */
    private String agentId;
}
