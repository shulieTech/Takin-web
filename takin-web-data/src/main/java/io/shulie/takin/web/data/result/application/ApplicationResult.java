package io.shulie.takin.web.data.result.application;

import java.util.Date;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-09-20
 */
@Data
public class ApplicationResult {
    // 应用 id
    private Long appId;
    // 应用名称
    private String appName;
    // 应用描述
    private String appSummary;
    // app最新发布版本
    private String appVersionCode;
    // 应用负责人
    private String appManagerName;
    // 应用最后修改时间
    private Date appUpdateTime;
    // 应用是否异常
    private Boolean appIsException;
    //应用中间接jar包列表
    private LibraryResult[] library;
    //实例节点数
    private InstanceInfoResult instanceInfo;
    private Long managerUserId;
}
