package io.shulie.takin.eventcenter.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/4/20 下午4:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskConfig extends ContextExt {
    /**
     * 场景ID
     */
    private Long sceneId;
    /**
     * 场景名称
     */
    private String sceneName;
    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 引擎类型
     */
    private EngineTypeEnum engineType;
    /**
     * ip数
     */
    private int agentNum;
    /**
     * 资源路径
     */
    private List<String> resPath;
    /**
     * 压测并发数
     */
    private int expectThroughput;
    /**
     * 压测时常
     */
    private long continuedTime;
    /**
     * 预热时长
     */
    private long preheatTime;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;
    /**
     * 业务活动
     */
    private Map<String, String> businessMap;
    /**
     * 是否主动停止
     */
    private Boolean forceStop = false;
    /**
     * 是否主动启动
     */
    private Boolean forceStart = true;
    /**
     * 拓展配置
     */
    private Map<String, Object> extendMap;
}
