package io.shulie.takin.cloud.common.constants;

/**
 * 压测请求流量明细上传方式
 *
 * @author xr.l
 */
public class PressureLogUploadConstants {

    /**
     * 通过cloud端上传，压测引擎生成ptl日志文件，cloud读取日志文件上传
     */
    public static final String UPLOAD_BY_CLOUD = "cloud";

    /**
     * 直接在压测引擎端上传，cloud只需要异步任务去处理状态即可
     */
    public static final String UPLOAD_BY_ENGINE = "engine";
}
