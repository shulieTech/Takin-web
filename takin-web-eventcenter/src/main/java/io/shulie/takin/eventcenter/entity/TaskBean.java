package io.shulie.takin.eventcenter.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/4/17 下午3:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskBean extends TaskConfig {
    /**
     * 进程号
     */
    private Long pid;
    /**
     * agent url
     */
    private List<String> znodes;
    /**
     * 重新拉起次数
     */
    private int retryTimes;
    /**
     * 开始压测时间
     */
    private long startTime;
    /**
     * 实际结束时间
     */
    private long currentTime;
    /**
     * 控制台地址
     */
    private String consoleUrl;
    /**
     * 任务启动配置
     */
    private RunConfig runConfig;
}
