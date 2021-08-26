package io.shulie.takin.web.biz.pojo.input.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 6:01 下午
 */
@Data
@Valid
public class ApplicationDsDeleteInput {
    @NotNull
    private Long id;
}
