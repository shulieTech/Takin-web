package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TIsolationAppConfigVO implements Serializable {

    /**
     * 应用id
     */
    private Long applicationId;

    private String applicationName;

    private List<Long> dubboRegistryIds;

    private List<Long> eurekaServerIds;

    private List<Long> rocketMQClusterIds;

    private List<Long> applicationIds;

    /**
     * 是否检查网络
     */
    private Integer checkNetwork;

    /**
     * mock隔离节点
     */
    private List<String> mockAppNodes;

    /**
     * 隔离应用开关
     */
    private Integer status;

    private Integer pageNum;

    private Integer pageSize;

}
