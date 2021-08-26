package io.shulie.takin.web.amdb.bean.result.leakverify;

import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class LeakVerifyDeployDetailDTO {

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
    private List<LeakObjectDTO> leakObjects;
}
