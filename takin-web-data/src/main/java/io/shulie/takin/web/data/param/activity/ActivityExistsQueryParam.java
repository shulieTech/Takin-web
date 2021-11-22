package io.shulie.takin.web.data.param.activity;

import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-30
 */
@Data
public class ActivityExistsQueryParam {

    private String activityName;

    private String applicationName;

    private String entranceName;

    private EntranceTypeEnum type;

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;

    private String virtualEntrance;

    private Integer activityType;
}
