package io.shulie.takin.web.biz.pojo.input.statistics;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/11/30 9:23 下午
 */
@Data
public class PressureTotalInput {
    private String type;
    private String startTime;
    private String endTime;
}
