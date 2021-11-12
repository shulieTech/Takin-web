package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 探针版本列表查询对象
 * @Author ocean_wll
 * @Date 2021/11/12 2:43 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参-探针版本列表查询类")
public class PluginLibraryListQueryRequest extends PageBaseDTO {

    @ApiModelProperty(value = "插件名")
    private String pluginName;
}