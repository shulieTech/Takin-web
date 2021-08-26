package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 5:55 下午
 */
@Data
@Valid
public class ApplicationDsEnableRequest {
    @NotNull
    private Long id;

    @NotNull
    private Integer status;
}
