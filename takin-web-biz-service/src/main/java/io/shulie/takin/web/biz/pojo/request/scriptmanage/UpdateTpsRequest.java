package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-01-28
 */
@Data
public class UpdateTpsRequest {

    @NotNull
    private Long reportId;

    @NotNull
    private Long sceneId;

    @NotNull
    private Long targetTps;
}
