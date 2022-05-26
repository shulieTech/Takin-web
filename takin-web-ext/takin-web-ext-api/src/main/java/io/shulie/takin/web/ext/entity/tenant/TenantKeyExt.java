package io.shulie.takin.web.ext.entity.tenant;

import lombok.Data;

@Data
public class TenantKeyExt {
    private Long tenantId;

    private String envCode;

    public String publicKey;

    /**
     * 私钥
     */
    public String privateKey;

    /**
     * 版本,每次版本加1
     */
    public int version;

    /**
     * 类型
     */
    public String type;
}
