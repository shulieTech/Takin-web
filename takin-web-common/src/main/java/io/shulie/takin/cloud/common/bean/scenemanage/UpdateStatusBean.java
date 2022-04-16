package io.shulie.takin.cloud.common.bean.scenemanage;

import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/18 下午6:24
 */
@Data
public class UpdateStatusBean {

    /**
     * 任务ID
     */
    private Long resultId;

    /**
     * 用于报告
     */
    private Integer preStatus;
    /**
     * 用于报告
     */
    private Integer afterStatus;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 客户Id 新增
     */
    private Long tenantId;

    /**
     * check 状态
     */
    private SceneManageStatusEnum[] checkEnum;
    /**
     * 更新至此状态
     */
    private SceneManageStatusEnum updateEnum;

    public UpdateStatusBean() {}

    /**
     * @param sceneId    场景主键
     * @param resultId   结果主键
     * @param tenantId   租户主键
     * @param updateEnum 更新枚举
     * @param checkEnums 最后一个参数 是check
     */
    public UpdateStatusBean(Long sceneId, Long resultId, Long tenantId, SceneManageStatusEnum updateEnum, SceneManageStatusEnum... checkEnums) {
        this.sceneId = sceneId;
        this.resultId = resultId;
        this.tenantId = tenantId;
        this.checkEnum = checkEnums;
        this.updateEnum = updateEnum;
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
        private SceneManageStatusEnum[] checkEnum;
        private SceneManageStatusEnum updateEnum;

        Builder(Long sceneId, Long resultId, Long tenantId) {
            this.sceneId = sceneId;
            this.resultId = resultId;
            this.tenantId = tenantId;
        }

        public Builder checkEnum(SceneManageStatusEnum... statusEnums) {
            this.checkEnum = statusEnums;
            return this;
        }

        public Builder updateEnum(SceneManageStatusEnum updateEnum) {
            this.updateEnum = updateEnum;
            return this;
        }

        public UpdateStatusBean build() {
            return new UpdateStatusBean(sceneId, resultId, tenantId, updateEnum, checkEnum);
        }
    }
}
