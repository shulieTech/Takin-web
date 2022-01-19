package io.shulie.takin.web.biz.pojo.request.scene;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.annotations.ApiModelProperty;

/**
 * 场景详情响应
 *
 * @author 张天赐
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneDetailResponse extends io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneDetailV2Response {
    /**
     * 基础信息
     */
    private BasicInfo basicInfo;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BasicInfo extends io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneRequest.BasicInfo {
        @ApiModelProperty(value = "是否定时执行")
        private Boolean isScheduler;
        @ApiModelProperty(name = "executeTime", value = "定时执行时间")
        private String executeTime;
    }
}
