package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/7/27 下午8:35
 */
@Data
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
}
