package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 探针配置生效范围
 * @Author ocean_wll
 * @Date 2021/8/13 10:00 上午
 */
@AllArgsConstructor
@Getter
public enum AgentConfigEffectTypeEnum {
    PROBE(0, "探针配置"),
    AGENT(1, "agent配置"),
    ;

    private Integer val;
    private String desc;
}
