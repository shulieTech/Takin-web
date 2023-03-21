package io.shulie.takin.web.biz.service.pts;

import io.shulie.takin.cloud.ext.content.script.ScriptNode;

/**
 * @author junshi
 * @ClassName PtsApiMatchService
 * @Description
 * @createTime 2023年03月16日 18:10
 */
public interface PtsApiMatchService {

    /**
     * 自动匹配业务活动
     * @param processId
     * @return
     */
    Integer autoMatchBusinessActivity(Long processId);

    /**
     * 前端带过来的业务活动
     * @param processId
     * @param activityId
     * @return
     */
    Integer markMatchBusinessActivity(Long processId, ScriptNode node, Long activityId);
}
