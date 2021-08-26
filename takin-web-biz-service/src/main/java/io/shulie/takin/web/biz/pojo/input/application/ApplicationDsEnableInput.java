package io.shulie.takin.web.biz.pojo.input.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 5:55 下午
 */
@Data
@Valid
public class ApplicationDsEnableInput {
    @NotNull
    private Long id;

    @NotNull
    private Integer status;
}
