package io.shulie.takin.web.biz.pojo.input.application;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 南风
 * @date 2021/11/10 5:45 下午
 */

@Valid
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationPluginDownloadPathInput {


    @ApiModelProperty("类型")
    @NotNull
    private String pathType;


    @ApiModelProperty("配置内容")
    @NotNull
    private String context;



}
