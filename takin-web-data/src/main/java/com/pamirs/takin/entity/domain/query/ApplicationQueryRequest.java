package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mubai , chengjiacai@shulie.io
 * @date 2020-03-16 15:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationQueryRequest extends PagingDevice implements Serializable {
    private static final long serialVersionUID = -5429714372789373890L;

    private String applicationName;

    private Long id;

    private List<Long> applicationIds;
}
