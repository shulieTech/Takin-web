package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步业务流程变更 - 请求 - 新
 *
 * @author 张天赐
 */
@Data
@ApiModel(value = "同步业务流程变更")
@EqualsAndHashCode(callSuper = true)
public class SynchronizeRequest extends ContextExt {
    @ApiModelProperty(value = "脚本实例主键")
    @NotNull(message = "脚本实例主键不能为空")
    private Long scriptId;
    @ApiModelProperty(value = "脚本解析结果")
    @NotNull(message = "脚本解析结果不能为空")
    private List<ScriptNode> analysisResult;
    @ApiModelProperty(value = "业务活动信息")
    @NotNull(message = "业务活动信息不能为空")
    private Map<String, BusinessActivityInfoData> businessActivityInfo;

    /**
     * 业务活动信息
     */
    @Data
    public static class BusinessActivityInfoData {
        /**
         * 业务活动主键
         */
        @NotNull(message = "业务活动主键不能为空")
        Long id;
        /**
         * 业务活动关联应用主键集合
         */
        @NotNull(message = "业务活动关联应用主键集合不能为空")
        List<String> applicationIdList;
    }
}