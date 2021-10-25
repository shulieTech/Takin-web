package io.shulie.takin.web.biz.pojo.response.activity;

import lombok.Data;

@Data
public class ActivityBottleneckResponse {

    // 总Rt 瓶颈类型(-1 没有瓶颈 | 1 一般卡慢 | 2 严重卡慢)
    private int allTotalRtBottleneckType;
    // 总Rt 瓶颈详情 id
    private Long rtBottleneckId;


    // 总成功率 瓶颈类型(-1 没有瓶颈 | 1 一般瓶颈 | 2 严重瓶颈)
    private int allSuccessRateBottleneckType;
    // 总成功率 瓶颈详情 id
    private Long successRateBottleneckId;


    // 总Rt 慢sql 瓶颈类型(-1 没有瓶颈 | 1 一般卡慢 | 2 严重卡慢)
    private int allSqlTotalRtBottleneckType;
    // 总Rt 慢sql 瓶颈详情 id
    private Long rtSqlBottleneckId;

}
