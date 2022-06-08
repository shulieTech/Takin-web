package io.shulie.takin.web.biz.pojo.input.application;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Data
@Valid
public class TenantDataSignInput {
    @NotNull
    private Long tenantId;

    @NotNull
    private Integer status;

}
