package io.shulie.takin.web.data.param.activity;

import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-30
 */
@Data
public class ActivityCreateParam extends UserCommonExt {

    private String activityName;

    private String applicationName;

    private Long applicationId;

    private String entranceName;

    private EntranceTypeEnum type;

    private Boolean isChange;

    private String changeBefore;

    private String changeAfter;

    private String activityLevel;

    private Integer isCore;

    private String businessDomain;

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;

    /**
     * 业务活动类型：0：正常业务活动 1：虚拟业务活动
     */
    private Integer businessType;

    /**
     * 入口地址
     */
    private String entrance;

    /**
     * 技术链路id
     */
    private Long linkId;

    /**
     * 绑定业务活动id
     */
    private Long bindBusinessId;

    /**
     * 虚拟业务mq类型
     */
    private EntranceTypeEnum serverMiddlewareType;

    private boolean persistence = true;

    /**
     * 业务分类Id
     */
    private Long category;
}
