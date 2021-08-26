package io.shulie.takin.web.biz.pojo.output.leakverify;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class LeakVerifyDeployDetailOutput {

    private Long id;

    /**
     * 漏数验证实例id
     */
    private Long leakVerifyDeployId;

    /**
     * 漏数类型
     */
    private String leakType;

    /**
     * 漏数数量
     */
    private Long leakCount;

    /**
     * 漏数内容
     */
    private String leakContent;
}
