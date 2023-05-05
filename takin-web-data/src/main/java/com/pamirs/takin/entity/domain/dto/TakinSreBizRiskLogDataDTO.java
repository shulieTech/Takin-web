package com.pamirs.takin.entity.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TakinSreBizRiskLogDataDTO implements Serializable {

    private String chainCode;
    private String chainCnName;
    private String entranceAppName;
    private String service;
    private String riskCategory;
    private String standardCode;
    private String standardName;
    private String calcResult;
    private String currentValue;
    private String currentValueUnit;
    private String invokeId;
    private String riskSubject;
    private String traceSampling;
}
