package io.shulie.takin.web.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author hezhongqi
 * @date 2021/11/17 00:04
 */
@AllArgsConstructor
@Getter
public enum ContextSourceEnum {
    FRONT(0, "前端"),
    AGENT(1, "第三方应用，agent,amdb"),
    JOB(2, "定时任务"),
    HREF(3, "前端直接location.href"),
    TENANT_SWITCH(4, "前端切换租户环境"),
    JOB_SCENE(5,"压测场景任务"),
    JOB_SCRIPT_DEBUG(6,"脚本调试任务"),
    JOB_FLOW_VERIFY(7,"业务活动流量验证任务"),
    JOB_INSPECT(8,"巡检任务")
    ;
    private Integer code;
    private String source;
}
