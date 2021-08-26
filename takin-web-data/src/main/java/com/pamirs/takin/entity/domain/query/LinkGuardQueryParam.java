package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import lombok.Data;

/**
 * @author 慕白
 * @date 2020-03-05 11:10
 */

@Data
public class LinkGuardQueryParam implements Serializable {
    private static final long serialVersionUID = -7807686616638257045L;

    private String applicationName;

    private String applicationId;

    private Long appId;

    private Long id;

    private Integer currentPage;

    private Integer pageSize;

    private String methodInfo;

    private Boolean isEnable;

}
