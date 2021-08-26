package io.shulie.takin.web.data.param.whitelist;

import java.util.Date;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/12 5:58 下午
 */
@Data
public class WhitelistUpdatePartAppNameParam {

    private Long id;

    private String interfaceName;

    private String type;

    private String effectiveAppName;

    private Date gmtModified;

    private Date gmtCreate;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

    private Long wlistId;


    public WhitelistUpdatePartAppNameParam() {
        this.gmtModified = new Date();
        this.gmtCreate = new Date();
    }
}
