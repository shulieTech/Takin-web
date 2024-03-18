package io.shulie.takin.web.amdb.bean.query.trace;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangz
 * Created on 2024/3/13 20:55
 * Email: zz052831@163.com
 */

@Data
public class TraceStatisticsQueryReq {
    @ApiModelProperty("接口名称")
    String serviceName;
    @ApiModelProperty("方法名称")
    String methodName;
    @ApiModelProperty("应用名称")
    String appName;
    @ApiModelProperty("查询开始时间")
    private String startTime;
    @ApiModelProperty("查询结束时间")
    private String endTime;

    /**
     * 租户标识
     */
    String tenant = "DEFAULT";

    /**
     * 用户ID
     */
    String userId = "SYSTEM";

    /**
     * 用户名称
     */
    String userName = "SYSTEM";
    /**
     * 租户标识
     */
    private String tenantAppKey;
    /**
     * 环境标识
     */
    private String envCode;

    public static final String DEFAULT_TENANT_KEY = "default";

    public static final String DEFAULT_ENV_CODE = "test";

    public static final String DEFAULT_USER_ID = "-1";
}
