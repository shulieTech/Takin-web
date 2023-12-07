package io.shulie.takin.web.biz.pojo.response.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian1
 * create: 2021-01-11
 */
@Data
@ApiModel
public class ActivityResponse {

    private Long activityId;

    // 业务活动名称
    private String activityName;

    // 所属应用
    private String applicationName;

    // 服务/入口
    private String entranceName;

    // 服务类型
    private EntranceTypeEnum type;

    /**
     * 绑定业务活动 用于虚拟活动切换正常业务活动
     */
    private EntranceTypeEnum bindType;

    private String changeBefore;

    private String changeAfter;

    private Boolean isChange;

    private Integer verifyStatus;

    private Boolean verifiedFlag;

    /**
     * 业务链路是否否核心链路 0:不是;1:是
     */
    @ApiModelProperty(name = "isCore", value = "业务活动链路是否核心链路 0:不是;1:是")
    private String isCore;

    @ApiModelProperty(name = "link_level", value = "业务活动等级")
    @JsonProperty("link_level")
    private String activityLevel;

    /**
     * 业务域: 0:订单域", "1:运单域", "2:结算域
     */
    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;

    @ApiModelProperty(name = "userId", value = "负责人ID")
    private Long userId;

    @ApiModelProperty(name = "userName", value = "负责人姓名")
    private String userName;

    private String extend;

    private String method;

    private String rpcType;

    private String serviceName;

    private String linkId;

    @ApiModelProperty(name = "topology", value = "拓扑图")
    private ApplicationEntranceTopologyResponse topology;

    @ApiModelProperty(name = "enableLinkFlowCheck", value = "是否开启流量验证")
    private boolean enableLinkFlowCheck;

    @ApiModelProperty(name = "enableLinkFlowCheck", value = "是否开启流量验证")
    private boolean flowCheckStatus;

    @ApiModelProperty(name = "virtualEntrance", value = "虚拟入口")
    private String virtualEntrance;

    /**
     * 业务活动类型
     */
    @ApiModelProperty(name = "businessType", value = "业务活动类型，正常：0，虚拟：1")
    private Integer businessType;

}
