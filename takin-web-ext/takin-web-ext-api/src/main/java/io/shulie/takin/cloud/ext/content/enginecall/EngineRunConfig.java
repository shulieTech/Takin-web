package io.shulie.takin.cloud.ext.content.enginecall;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.ext.content.AbstractEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 压测引擎启动配置
 *
 * @author liyuanba
 * @date 2021/10/29 2:47 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EngineRunConfig extends AbstractEntry {
    private Long sceneId;
    private Long taskId;
    private Long customerId;
    /**
     * 数据上报地址
     */
    private String consoleUrl;
    /**
     * cloud回调地址
     */
    private String callbackUrl;
    /**
     * 启动的pod数量
     */
    private Integer podCount;
    /**
     * 脚本文件完整路径和文件名
     */
    private String scriptFile;
    /**
     * 脚本文件所在目录
     */
    private String scriptFileDir;
    /**
     * 是否是在本地启动
     */
    private Boolean isLocal;
    /**
     * 调度任务路径
     */
    private String taskDir;
    /**
     * 施压场景：常规（压测场景页面），试跑，巡检
     */
    private Integer pressureScene;
    /**
     * 压测时长
     */
    private Long continuedTime;
    /**
     * 并发线程数
     */
    private Integer expectThroughput;
    /**
     * 压测引擎插件文件位置  一个压测场景可能有多个插件 一个插件也有可能有多个文件
     */
    private List<String> enginePluginsFiles;
    /**
     * 压测配置信息
     */
    private EnginePressureConfig pressureConfig;
    /**
     * 文件
     */
    private List<ScheduleStartRequestExt.DataFile> fileSets;
    /**
     * 业务活动配置的目标信息
     */
    private Map<String, BusinessActivityExt> businessMap;
    /**
     * 是否通过xpath的md5进行关联，新老板区分
     */
    private Boolean bindByXpathMd5;
    /**
     * 压测引起java虚拟机内存配置
     */
    private String memSetting;

}
