package com.pamirs.takin.entity.domain.dto.report;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/6 下午4:53
 */
@Data
public class ReportApplicationDTO {

    private ReportDetailDTO reportDetail;

    private List<String> applicationNames = Lists.newArrayList();
}
