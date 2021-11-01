package io.shulie.takin.web.biz.pojo.input.application;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author fanxx
 * @date 2020/11/27 5:55 下午
 */
@Data
@Valid
public class ApplicationDsEnableInputV2 {
    @NotNull
    private Long id;

    @NotNull
    private Integer status;

    @NotNull
    private Boolean isNewData;

    @NotNull
    private Long applicationId;

    @NotNull
    private String middlewareType;


}
