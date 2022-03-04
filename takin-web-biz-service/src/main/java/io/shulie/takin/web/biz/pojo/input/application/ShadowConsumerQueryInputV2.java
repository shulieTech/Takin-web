package io.shulie.takin.web.biz.pojo.input.application;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumerQueryInputV2 extends PagingDevice {


    private String type;

    private String shadowconsumerEnable;

    private String topicGroup;

    @NotNull
    private Long applicationId;
}
