package io.shulie.takin.web.biz.pojo.input.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 南风
 * @date 2021/09/02 3:23 下午
 */
@Data
@Valid
@ApiModel
public class ApplicationDsTemplateQueryInputV2 {
    @ApiModelProperty(value = "applicationName",name = "应用名",required = true)
    private String applicationName;

    @ApiModelProperty(name = "数据版本", value = "isNewData")
    private Boolean isNewData = true;

    @ApiModelProperty(value = "agentSourceType",name = "来源类型",required = true)
    @NotNull
    private String agentSourceType;

    @ApiModelProperty(value = "dsType",name = "影子方案类型",required = true)
    @NotNull
    private Integer dsType;

    @ApiModelProperty(value = "cacheType",name = "缓存模式")
    private String cacheType;

    @ApiModelProperty(value = "connectionPool",name = "中间件名称")
    private String connectionPool;


}
