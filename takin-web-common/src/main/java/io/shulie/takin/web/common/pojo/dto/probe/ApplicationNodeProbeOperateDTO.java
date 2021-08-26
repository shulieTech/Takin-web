package io.shulie.takin.web.common.pojo.dto.probe;

import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/8 5:31 下午
 */
@Data
public class ApplicationNodeProbeOperateDTO {

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 操作类型
     */
    private Integer operateType;

    /**
     * 探针记录id
     */
    private Long probeId;

}
