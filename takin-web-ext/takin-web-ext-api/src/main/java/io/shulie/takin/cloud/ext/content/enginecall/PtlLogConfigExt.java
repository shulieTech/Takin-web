package io.shulie.takin.cloud.ext.content.enginecall;

import io.shulie.takin.cloud.ext.content.AbstractEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liyuanba
 * @date 2021/11/3 9:50 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PtlLogConfigExt extends AbstractEntry {

    /**
     * 日志上传位置，engine代表从压测引擎上传，cloud代表从cloud上传
     */
    private String ptlUploadFrom;

    /**
     * 是否输出ptl日志文件
     */
    private boolean ptlFileEnable;

    /**
     * ptl日志文件是否只输出错误信息
     */
    private boolean ptlFileErrorOnly;

    /**
     * ptl日志是否只输出接口调用时间较长信息
     */
    private boolean ptlFileTimeoutOnly;

    /**
     * ptl日志接口超时阈值
     */
    private Long timeoutThreshold;

    /**
     * ptl日志是否截断
     */
    private boolean logCutOff;
}
