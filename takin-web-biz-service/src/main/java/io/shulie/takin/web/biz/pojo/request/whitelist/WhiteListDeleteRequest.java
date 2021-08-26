package io.shulie.takin.web.biz.pojo.request.whitelist;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-01
 */
@Data
public class WhiteListDeleteRequest {

    @NotEmpty
    private List<Long> dbIds;
}
