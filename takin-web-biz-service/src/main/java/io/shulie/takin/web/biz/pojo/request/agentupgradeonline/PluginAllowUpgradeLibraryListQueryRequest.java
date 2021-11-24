package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description 探针允许升级的版本列表查询对象
 * @Author 南风
 * @Date 2021/11/17 2:43 下午
 */
@Data
@ApiModel("入参-探针允许升级版本列表查询类")
public class PluginAllowUpgradeLibraryListQueryRequest {

    @ApiModelProperty(value = "插件名",required = true)
    @NotNull
    private String pluginName;

    @ApiModelProperty(value = "应用id集合",required = true)
    @NotEmpty
    private List<Long> applicationIds;
}