package io.shulie.takin.web.biz.pojo.request.application;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author fanxx
 * @date 2020/11/27 5:23 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Valid
public class ApplicationDsUpdateRequest extends ApplicationDsCreateRequest {

    /**
     * 配置id
     */
    @NotNull
    private Long id;
}
