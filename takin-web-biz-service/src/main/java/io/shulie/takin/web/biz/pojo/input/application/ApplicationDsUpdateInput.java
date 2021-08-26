package io.shulie.takin.web.biz.pojo.input.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/11/27 5:23 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Valid
public class ApplicationDsUpdateInput extends ApplicationDsCreateInput {

    /**
     * 配置id
     */
    @NotNull
    private Long id;
}
