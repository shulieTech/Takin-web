package io.shulie.takin.cloud.common.enums;

public enum PressureTaskStateEnum {
    INITIALIZED(0, "初始化"),//初始化
    CHECKED(1, "检测完成"),
    RESOURCE_LOCKING(2, "资源锁定中"),
    RESOURCE_LOCK_FAILED(3, "资源锁定失败"),
    STARTING(4, "启动中"),//启动中
    ALIVE(5, "启动完成"),//启动完成
    PRESSURING(6, "压测中"),//压测中
    UNUSUAL(7, "异常"),//异常
    STOPPING(8, "停止中"),//停止中
    INACTIVE(9, "停止"),//停止
    REPORT_GENERATING(10, "报告生成中"),//报告生成中
    REPORT_DONE(11, "报告生成完成"),//报告生成完成
    ;

    private final int code;
    private final String desc;

    PressureTaskStateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
