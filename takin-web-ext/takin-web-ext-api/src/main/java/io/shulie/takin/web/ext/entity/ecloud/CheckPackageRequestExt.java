package io.shulie.takin.web.ext.entity.ecloud;

import lombok.Data;

@Data
public class CheckPackageRequestExt {

    // 并发数 并发模式实际并发数，TPS模式并发数等于TPS/2，多线程组累加
    private Integer threadNum;
    // 压测时长
    private Integer duration;

    private Integer podNum;

    private Long tenantId;
}
