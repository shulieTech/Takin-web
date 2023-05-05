package com.pamirs.takin.entity.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TakinSreBizRiskLogDTO implements Serializable {

    private Boolean success;

    private String errorMsg;

    private List<TakinSreBizRiskLogDataDTO> data;
}
