package io.shulie.takin.web.biz.pojo.input.application;

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
public class ApplicationPluginDownloadPathUpdateInput extends ApplicationPluginDownloadPathInput{

    @NotNull
    private Long id;


}
