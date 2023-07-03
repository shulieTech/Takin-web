package io.shulie.takin.web.data.result.application;

import java.util.Date;

import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/12/8 3:11 下午
 */
@Data
public class ApplicationListResult {

    /**
     * 应用id
     */
    private Long applicationId;

    private String applicationName;

    private String userName;

    private Date updateTime;

    private Long deptId;

    private Integer accessStatus;

    /**
     * 节点数量
     */
    private Integer nodeNum;

    private Integer confCheckStatus;

}
