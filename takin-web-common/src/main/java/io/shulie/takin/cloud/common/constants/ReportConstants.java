package io.shulie.takin.cloud.common.constants;

/**
 * 报表状态
 *
 * @author 莫问
 * @date 2020-04-24
 */
public class ReportConstants {

    /**
     * 就绪状态
     **/
    public static final int INIT_STATUS = 0;

    /**
     * 生成中
     **/
    public static final int RUN_STATUS = 1;

    /**
     * 完成
     **/
    public static final int FINISH_STATUS = 2;

    /**
     * 锁定
     **/
    public static final int LOCK_STATUS = 9;

    //压测失败
    //public static final int FAIL_STATUS = 3;

    /**
     * 压测信息消息 记录本次压测过程中的异常信息
     */
    public static final String PRESSURE_MSG = "pressure_msg";

    /**
     * 报表结论:通过
     **/
    public static final int PASS = 1;

    /**
     * 报表结论:不通过
     **/
    public static final int FAIL = 0;

    /**
     * 异常消息
     */
    public static final String FEATURES_ERROR_MSG = "error_msg";

    /**
     * sla熔断数据
     */
    public static final String SLA_ERROR_MSG = "sla_msg";

    /**
     * influxDB没5秒汇总的数据
     */
    public static final String ALL_BUSINESS_ACTIVITY = "all";
    /**
     * 根测试计划的MD5
     */
    public static final String TEST_PLAN_MD5 = "0f1a197a2040e645dcdb4dfff8a3f960";

}
