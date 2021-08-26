package io.shulie.takin.web.data.result.leakverify;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@Deprecated

public class LeakVerifyDeployDetailResult {

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
    private List<LeakObjectResult> leakObjectResults;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String feature;
}
