package io.shulie.takin.web.biz.pojo.request.scene;

import java.util.List;

import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 场景详情响应
 *
 * @author 张天赐
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneDetailResponse extends io.shulie.takin.adapter.api.model.response.scenemanage.SceneDetailV2Response {
    /**
     * 基础信息
     */
    private BasicInfo basicInfo;

    /**
     * 验证信息
     */
    private DataValidation dataValidation;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BasicInfo extends io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.BasicInfo {
        @ApiModelProperty(value = "是否定时执行")
        private Boolean isScheduler;
        @ApiModelProperty(name = "executeTime", value = "定时执行时间")
        private String executeTime;
    }

    @Getter
    @Setter
    public static class DataValidation extends SceneRequest.DataValidation {
        @ApiModelProperty("排除的应用id列表")
        private List<String> excludedApplicationIds;
    }

}
