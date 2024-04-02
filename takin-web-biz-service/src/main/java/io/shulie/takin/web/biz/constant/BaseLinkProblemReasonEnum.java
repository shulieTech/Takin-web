package io.shulie.takin.web.biz.constant;

/**
 * @author zhangz
 * Created on 2024/3/15 13:48
 * Email: zz052831@163.com
 */

public enum BaseLinkProblemReasonEnum {

    NONE_NODE("当前业务活动存在性能基线中不存在的调用节点"),
    NODE_RT_HIGH("当前业务活动的节点rt比性能基线的节点rt高"),
    NODE_SUCCESS_RATE_LOW("当前业务活动节点的成功率比性能基线的节点低");


    private String reason;

    BaseLinkProblemReasonEnum(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
