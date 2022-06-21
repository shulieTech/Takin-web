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

    private String callbackUrl;

    private Integer workRecordId;


    @Data
    public class ApplicationActivity {

        private String activityName;

        private String applicationName;

        private String method;

        private Integer rpcType;

        private String serviceName;
    }

    @Data
    public class ActivityFIle {

        private String name;

        private Integer type;

        private String path;
    }
}
