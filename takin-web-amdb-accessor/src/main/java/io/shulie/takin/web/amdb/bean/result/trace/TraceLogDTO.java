package io.shulie.takin.web.amdb.bean.result.trace;

import java.io.Serializable;

import com.pamirs.pradar.log.parser.trace.RpcBased;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Package io.shulie.takin.web.amdb.bean.result.trace
* @ClassName: TraceLogDTO
* @author hezhongqi
* @description:
* @date 2021/10/27 10:11
*/
@Data
public class TraceLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("appName")
    private String appName;

    @ApiModelProperty("async")
    private Boolean async;

    @ApiModelProperty("attributes")
    private String attributes;

    @ApiModelProperty("callbackMsg")
    private String callbackMsg;

    @ApiModelProperty("clusterTest")
    private Boolean clusterTest;

    @ApiModelProperty("cost")
    private Integer cost;

    @ApiModelProperty("dateToMin")
    private Integer dateToMin;

    @ApiModelProperty("entranceId")
    private String entranceId;

    @ApiModelProperty("entranceNodeId")
    private String entranceNodeId;

    @ApiModelProperty("entranceServiceType")
    private String entranceServiceType;

    @ApiModelProperty("hostIp")
    private String hostIp;

    @ApiModelProperty("index")
    private Integer index;

    @ApiModelProperty("level")
    private Integer level;

    @ApiModelProperty("localAttributes")
    private String localAttributes;

    @ApiModelProperty("localId")
    private String localId;

    @ApiModelProperty("log")
    private String log;

    @ApiModelProperty("logType")
    private Integer logType;

    /**
     * 方法名称
     */
    @ApiModelProperty("methodName")
    private String methodName;

    @ApiModelProperty("middlewareName")
    private String middlewareName;

    @ApiModelProperty("parentIndex")
    private Integer parentIndex;

    @ApiModelProperty("parsedAppName")
    private String parsedAppName;

    @ApiModelProperty("parsedExtend")
    private String parsedExtend;

    @ApiModelProperty("parsedMethod")
    private String parsedMethod;

    @ApiModelProperty("parsedServiceName")
    private String parsedServiceName;

    @ApiModelProperty("port")
    private String port;

    @ApiModelProperty("remoteIp")
    private String remoteIp;

    @ApiModelProperty("request")
    private String request;

    @ApiModelProperty("requestSize")
    private Integer requestSize;

    @ApiModelProperty("response")
    private String  response;

    @ApiModelProperty("responseSize")
    private Integer responseSize;

    @ApiModelProperty("resultCode")
    private String resultCode;

    @ApiModelProperty
    private RpcBased rpcBased;


}
