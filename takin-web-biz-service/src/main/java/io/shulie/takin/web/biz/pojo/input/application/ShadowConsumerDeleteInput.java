package io.shulie.takin.web.biz.pojo.input.application;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumerDeleteInput {

    @NotNull
    private List<Long> ids;
}
