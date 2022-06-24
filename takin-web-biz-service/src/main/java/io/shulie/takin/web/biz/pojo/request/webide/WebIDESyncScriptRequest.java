package io.shulie.takin.web.biz.pojo.request.webide;

import lombok.Data;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2022/6/20 1:36 下午
 */
@Data
public class WebIDESyncScriptRequest {

    private List<ApplicationActivity> application;

    private List<ActivityFIle> file;

    private Integer requestNum;

    private Integer concurrencyNum;

    private String callbackAddr;

    private Long workRecordId;


    @Data
    public static class ApplicationActivity {

        private String activityName;

        private String applicationName;

        private String method;

        private Integer rpcType;

        private String serviceName;
    }

    @Data
    public static class ActivityFIle {

        private String name;

        private Integer type;

        private String path;
    }
}
