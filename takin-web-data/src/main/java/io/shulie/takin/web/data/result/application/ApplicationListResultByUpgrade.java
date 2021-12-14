package io.shulie.takin.web.data.result.application;

import lombok.Data;

import java.util.Date;

@Data
public class ApplicationListResultByUpgrade {

    /**
     * 应用id
     */
    private Long applicationId;

    private String applicationName;

    private String userName;

    private Date updateTime;

    private Integer nodeNum;

}
