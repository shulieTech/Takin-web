package io.shulie.takin.eventcenter.entity;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/4/20 上午10:20
 */
@Data
public class RunConfig {
    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * zookeeper地址
     */
    private String zkServer;

    /**
     * 压测任务启动所需内存
     */
    private String memory;

    /**
     * 引擎类型
     */
    private EngineTypeEnum engineType;

    /**
     * 压测任务运行所在机器IP
     */
    private String ip;

    /**
     * 任务运行agent url
     */
    private String znode;
}
