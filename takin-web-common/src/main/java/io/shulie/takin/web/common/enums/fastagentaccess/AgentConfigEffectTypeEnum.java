package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 探针配置生效范围
 *
 * @author ocean_wll
 * @date 2021/8/13 10:00 上午
 */
@AllArgsConstructor
@Getter
public enum AgentConfigEffectTypeEnum {
    /**
     * 探针配置
     */
    PROBE(0, "探针配置"),
    /**
     * agent配置
     */
    AGENT(1, "agent配置"),
    ;

    private Integer val;
    private String desc;
}
