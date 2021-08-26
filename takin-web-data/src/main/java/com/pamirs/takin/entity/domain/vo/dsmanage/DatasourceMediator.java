package com.pamirs.takin.entity.domain.vo.dsmanage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fanxx
 * @date 2020/3/13 上午11:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatasourceMediator {
    private String dataSourceBusiness;
    private String dataSourcePerformanceTest;
}
