package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-03-16 15:16
 */

@Data
public class ApplicationQueryParam implements Serializable {
    private static final long serialVersionUID = -5429714372789373890L;

    private String applicationName;

    private Long id;

    private Integer currentPage;

    private Integer pageSize;

    private List<Long> applicationIds;
}
