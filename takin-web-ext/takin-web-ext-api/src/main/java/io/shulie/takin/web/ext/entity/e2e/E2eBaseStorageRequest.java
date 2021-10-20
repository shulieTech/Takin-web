package io.shulie.takin.web.ext.entity.e2e;

import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/9/14 5:47 下午
 * @Description:
 */
@Data
public class E2eBaseStorageRequest {
    /**
     * 响应时间
     */
    private double rt;

    /**
     * 成功率
     */
    private double successRate;
    /**
     * 慢sql的最大rt值
     */
    private double maxRt;
}
