package io.shulie.takin.web.data.param.activity;

import java.util.List;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-30
 */
@Data
public class ActivityQueryParam extends AuthQueryParamCommonExt {

    private String activityName;

    private String domain;

    private Integer isChange;

    private String linkLevel;

    //private List<Long> userIdList;

    /**
     * 业务活动 类型
     */
    private Integer businessType;

    /**
     * 业务活动 ids
     */
    private List<Long> activityIds;

    /**
     * 链路入口
     */
    private String entrance;

    /**
     * 多链路入口
     */
    private List<String> entranceList;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * 类型
     */
    private Integer type;
}
