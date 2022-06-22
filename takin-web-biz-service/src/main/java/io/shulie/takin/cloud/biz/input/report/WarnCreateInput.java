package io.shulie.takin.cloud.biz.input.report;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/11/18 上午11:49
 */
@Data
public class WarnCreateInput {

    private Long id;

    private Long ptId;

    private Long slaId;

    private String slaName;

    private Long businessActivityId;

    private String businessActivityName;

    private String warnContent;

    private Double realValue;

    private String bindRef;

    private String warnTime;
}
