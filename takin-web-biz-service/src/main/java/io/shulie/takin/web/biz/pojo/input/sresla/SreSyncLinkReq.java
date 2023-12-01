package io.shulie.takin.web.biz.pojo.input.sresla;

import lombok.Data;

/**
 * @author zhaoyong
 * 同步链路参数
 */
@Data
public class SreSyncLinkReq {

    private String chainCode;

    private String chainName;

    private String chainCnName;

    private String biz;

    private String system;

    private String entranceServiceId;

    private String entranceAppName;

    private String owner;

    private boolean important;

    private String bizPriority;

    private Integer costTarget;

    private String tenantCode;

    private String envCode;
}
