package io.shulie.takin.web.data.param.leakcheck;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 2:32 下午
 */
@Data
public class LeakCheckConfigCreateParam {
    /**
     * 业务活动id
     */
    private Long businessActivityId;

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * sql id列表
     */
    private List<Long> sqlIdList;
}
