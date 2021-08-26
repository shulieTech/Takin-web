package io.shulie.takin.web.data.param.leakcheck;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 2:32 下午
 */
@Data
public class LeakCheckConfigQueryParam {
    /**
     * 业务活动id
     */
    private List<Long> businessActivityIds;

    /**
     * 数据源id
     */
    private List<Long> datasourceIds;
}
