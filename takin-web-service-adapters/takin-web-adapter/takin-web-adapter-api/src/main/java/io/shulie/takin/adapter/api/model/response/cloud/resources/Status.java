package io.shulie.takin.adapter.api.model.response.cloud.resources;

public enum Status {
    INITIALIZED,//初始化
    STARTING,//启动中
    ALIVE,//启动完成
    PRESSURING,//压测中
    UNUSUAL,//异常
    STOPPING,//停止中
    INACTIVE;//停止
}
