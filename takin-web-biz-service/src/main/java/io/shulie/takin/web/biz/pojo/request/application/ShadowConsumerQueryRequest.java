package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumerQueryRequest extends PagingDevice {

    private String type;

    private Boolean enabled;

    private String topicGroup;

    @NotNull
    private Long applicationId;
}
