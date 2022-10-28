package io.shulie.takin.web.amdb.bean.result.application;

import lombok.Data;

/**
 * 兼容agent1.0 agent在zk上存储的内容
 *
 */
@Data
public class ApplicationNodeZkDTO {
    /**
     * {
     *     "agentId":"192.168.1.214-13395",
     *     "address":"192.168.1.214",
     *     "jdkVersion":"1.8.0_271",
     *     "gcType":"Parallel",
     *     "errorCode":"",
     *     "jars":"/data/canace/apps/easy-demo/pradar-agent/agent/pradar-core-ext-bootstrap-1.0.0.jar;./druid-1.1.16-1.0-SNAPSHOT.jar;",
     *     "pid":"13395",
     *     "agentLanguage":"JAVA",
     *     "userId":"164",
     *     "errorMsg":"Pradar Cluster Tester is not ready now:canace-xn-test-st",
     *     "host":"172.17.0.1",
     *     "name":"13395@localhost",
     *     "agentVersion":"4.4.0",
     *     "startTime":"1661242058841",
     *     "md5":"d41091f83f4482c448ed706f67fff464\n",
     *     "status":"false"
     * }
     */
    private String agentId;
    private String address;
    private String jdkVersion;
    private String gcType;
    private String errorCode;
    private String jars;
    private String pid;
    private String agentLanguage;
    private String userId;
    private String errorMsg;
    private String host;
    private String name;
    private String agentVersion;
    private String startTime;
    private String md5;
    private boolean status;
}
