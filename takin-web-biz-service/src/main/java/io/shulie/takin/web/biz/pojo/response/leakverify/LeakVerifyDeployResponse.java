package io.shulie.takin.web.biz.pojo.response.leakverify;

import java.io.Serializable;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaoyong
 */
@Data
@Deprecated
@ApiModel(value = "LeakVerifyDeployResponse", description = "漏数实例")
public class LeakVerifyDeployResponse implements Serializable {
    private static final long serialVersionUID = 834522014546469149L;

    @ApiModelProperty(value = "漏数实例id")
    private Long id;

    /**
     * 漏数记录id
     */
    @ApiModelProperty(value = "漏数记录id")
    private Long leakVerifyId;

    /**
     * 应用名
     */
    @ApiModelProperty(value = "应用名")
    private String applicationName;

    /**
     * 链路入口名称
     */
    @ApiModelProperty(value = "链路入口名称")
    private String entryName;

    /**
     * 压测请求数量
     */
    @ApiModelProperty(value = "压测请求数量")
    private Long pressureRequestCount;

    /**
     * 压测漏数数量
     */
    @ApiModelProperty(value = "压测漏数数量")
    private Long pressureLeakCount;

    /**
     * 业务请求数量
     */
    @ApiModelProperty(value = "业务请求数量")
    private Long bizRequestCount;

    /**
     * 业务漏数数量
     */
    @ApiModelProperty(value = "业务漏数数量")
    private Long bizLeakCount;
}
