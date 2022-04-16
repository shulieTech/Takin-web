package io.shulie.takin.adapter.api.model.response.statistics;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/11/30 9:21 下午
 */
@Data
public class ReportTotalResp {
    private Integer count;
    private Integer success;
    private Integer fail;
}
