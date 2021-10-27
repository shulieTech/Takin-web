package io.shulie.takin.web.biz.pojo.input.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 南风
 * @date 2021/09/02 3:23 下午
 */
@Data
@Valid
@ApiModel
public class ApplicationDsUpdateInputV2 extends ApplicationDsCreateInputV2{

    @ApiModelProperty(name = "数据版本", value = "isNewData")
    private Boolean isNewData = true;

    /**
     * 配置id
     */
    @NotNull
    @ApiModelProperty(name = "记录id", value = "id",required =true)
    private Long id;



}
