package io.shulie.takin.cloud.common.enums.scenemanage;

import io.shulie.takin.adapter.api.model.common.SlaBean;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/6/24 10:44 上午
 */
@AllArgsConstructor
@Getter
public enum SceneStopReasonEnum {
    // 名称-对象触发了熔断机制。
    SLA("SLA触发熔断机制", "[%s]-[%s]触发了熔断机制"),
    PRESSURE_NODE("压力节点异常", "%s"),
    ENGINE("压测引擎异常", "%s");
    private String type;
    /**
     * 格式定义
     */
    private String description;

    /**
     * 转sla格式
     *
     * @param slaBean -
     * @return -
     */
    public static String toSlaDesc(SlaBean slaBean) {
        return String.format(SLA.description, slaBean.getRuleName(), slaBean.getBusinessActivity());
    }

    /**
     * 转压测引擎格式
     *
     * @param error -
     * @return -
     */
    public static String toEngineDesc(String error) {
        return String.format(ENGINE.description, error);
    }

    /**
     * 普通转换
     *
     * @param error -
     * @return -
     */
    public static String toDesc(String error) {
        return String.format("%s", error);
    }

}
