package io.shulie.takin.web.api.constant;

/**
 * @author caijianying
 */
public class RemoteUrls {
    /**
     * 远程登录
     */
    public static final String REMOTE_LOGIN = "api/loginNoCode";

    /**
     * 租户信息
     */
    public static final String REMOTE_TENANT_INFO = "api/tenantInfo";


    /**
     * 启动压测
     */
    public static final String START_TASK = "api/scene/task/start";

    /**
     * 场景列表
     */
    public static final String SCENE_LIST = "api/scenemanage/list";

    /**
     * 场景详情
     */
    public static final String SCENE_DETAIL = "api/v2/scene/detail";

    /**
     * 查询任务状态
     */
    public static final String QUERY_TASK_STATUS = "api/scene/task/checkStartStatus";

    /**
     * 查询报告结果
     */
    public static final String QUERY_REPORT_RESULT = "api/report/businessActivity/summary/list";
}
