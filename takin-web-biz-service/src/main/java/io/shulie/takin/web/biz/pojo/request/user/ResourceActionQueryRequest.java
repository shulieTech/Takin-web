package io.shulie.takin.web.biz.pojo.request.user;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/5 7:25 下午
 */
@Data
public class ResourceActionQueryRequest {

    @NotNull(message = "roleId不为空")
    private Long roleId;

}
