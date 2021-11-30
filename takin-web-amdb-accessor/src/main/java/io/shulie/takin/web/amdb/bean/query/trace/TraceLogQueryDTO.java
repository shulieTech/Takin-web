package io.shulie.takin.web.amdb.bean.query.trace;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.amdb.bean.query.trace
 * @ClassName: TraceLogQueryDTO
 * @Description: TODO
 * @Date: 2021/10/25 17:30
 */
@Data
public class TraceLogQueryDTO extends PagingDevice {

    private String startTime;

    private String endTime;

    /**
     * 查询当前租户的应用
     */
    private List<String> appNames;

    @ApiModelProperty("应用名")
    private String appName;

    @ApiModelProperty("服务名")
    private String serviceName;

    @ApiModelProperty("traceId")
    private String traceId;

    /**
     * tenantAppKey
     */
    private String tenantAppKey;

    /**
     * 环境编码
     */
    private String envCode;


}
