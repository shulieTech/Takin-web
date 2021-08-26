package io.shulie.takin.web.data.param.application;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mubai
 * @date 2020-09-23 19:10
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationNodeQueryParam extends PagingDevice {
    private Long applicationId;
    private List<String> applicationNames;
    private String ip;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 探针状态, 0-已安装,1-未安装,2-安装中,3-卸载中,4-安装失败,99-未知状态
     */
    private Integer probeStatus;

}
