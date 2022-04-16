package io.shulie.takin.cloud.ext.content.enginecall;

import java.util.Map;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-05-14
 */
@Data
public class ScheduleResponseExt {

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 状态描述
     */
    private String errorMgs;

    /**
     * 拓展配置
     */
    private Map<String, Object> extendMap;

}
