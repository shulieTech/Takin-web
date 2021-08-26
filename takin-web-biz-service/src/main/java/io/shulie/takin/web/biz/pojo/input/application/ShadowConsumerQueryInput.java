package io.shulie.takin.web.biz.pojo.input.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumerQueryInput extends PagingDevice {

    private ShadowMqConsumerType type;

    private Boolean enabled;

    private String topicGroup;

    @NotNull
    private Long applicationId;
}
