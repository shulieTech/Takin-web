package io.shulie.takin.web.data.result.activity;

import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-30
 */
@Data
public class ActivityResult {

    private Long activityId;

    private String activityName;

    private String applicationName;

    private String entranceName;

    private EntranceTypeEnum type;

    private String changeBefore;

    private String changeAfter;

    private Boolean isChange;

    private Long userId;

    private Long tenantId;

    private String envCode;

    /**
     * 业务链路是否否核心链路 0:不是;1:是
     */
    private Integer isCore;

    private String activityLevel;

    /**
     * 业务域: 0:订单域", "1:运单域", "2:结算域
     */
    private String businessDomain;

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;

    /**
     * 绑定业务活动id
     */
    private Long bindBusinessId;

    /**
     * 技术链路 id
     */
    private Long linkId;

    /**
     * 业务活动类型
     */
    private Integer businessType;

    private String virtualEntrance;

    /**
     * kafka,rabbitmq,http.....
     */
    private String serverMiddlewareType;


}
