package io.shulie.takin.web.data.param.leakcheck;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 4:35 下午
 */
@Data
public class LeakCheckConfigSingleQueryParam {

    /**
     * 业务活动id
     */
    private Long businessActivityId;

    /**
     * 数据源id
     */
    private Long datasourceId;
}
