package com.pamirs.takin.entity.domain.dto.report;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportMessageStatusCodeDTO implements Serializable {

    private String statusCode;

    private String statusName;
}
