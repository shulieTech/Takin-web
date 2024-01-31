package io.shulie.takin.web.data.result.application;

import lombok.Data;

import java.util.Date;

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

    private String nickName;

    private Date updateTime;

    private Integer accessStatus;

    /**
     * 节点数量
     */
    private Integer nodeNum;

    private Long userId;

}
