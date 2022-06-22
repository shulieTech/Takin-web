package com.pamirs.takin.cloud.entity.domain.vo.scenemanage;

import lombok.Data;

/**
 * @author 何仲奇
 * TODO 新增一张表，用于记录启动记录
 * @date 2020/9/24 9:57 上午
 */
@Data
public class SceneManageStartRecordVO {
    /**
     * 任务ID
     */
    private Long resultId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 客户Id 新增
     */
    private Long tenantId;

    private Boolean success;

    private String errorMsg;

    public SceneManageStartRecordVO(Long resultId, Long sceneId, Long tenantId, Boolean success,
        String errorMsg) {
        this.resultId = resultId;
        this.sceneId = sceneId;
        this.tenantId = tenantId;
        this.success = success;
        this.errorMsg = errorMsg;
    }

    /**
     * create Builder method
     **/
    public static Builder build(Long sceneId, Long resultId, Long tenantId) {
        return new Builder(sceneId, resultId, tenantId);
    }

    public static class Builder {
        private Long resultId;
        private Long sceneId;
        private Long tenantId;
        private Boolean success;
        private String errorMsg;

        Builder(Long sceneId, Long resultId, Long tenantId) {
            this.sceneId = sceneId;
            this.resultId = resultId;
            this.tenantId = tenantId;
        }

        public Builder success(Boolean success) {
            this.success = success;
            return this;
        }

        public Builder errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public SceneManageStartRecordVO build() {
            return new SceneManageStartRecordVO(resultId, sceneId, tenantId, success, errorMsg);
        }
    }

}
