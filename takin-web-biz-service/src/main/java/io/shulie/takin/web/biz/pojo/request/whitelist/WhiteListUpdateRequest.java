package io.shulie.takin.web.biz.pojo.request.whitelist;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-01
 */
@Data
public class WhiteListUpdateRequest {

    @NotNull
    private Long dbId;

    @NotBlank
    private String interfaceName;

    @NotBlank
    private String interfaceType;
}
