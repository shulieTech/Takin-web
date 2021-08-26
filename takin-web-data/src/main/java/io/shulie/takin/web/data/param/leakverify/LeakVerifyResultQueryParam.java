package io.shulie.takin.web.data.param.leakverify;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 8:17 下午
 */
@Data
public class LeakVerifyResultQueryParam {
    /**
     * 引用类型
     */
    private Integer refType;

    /**
     * 引用id
     */
    private Long refId;

    /**
     * 报告id
     */
    private Long reportId;
}
