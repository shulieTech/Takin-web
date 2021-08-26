package io.shulie.takin.web.biz.pojo.request.user;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/5 9:26 下午
 */
@Data
@Valid
public class ResourceScopeQueryRequest implements Serializable {

    private static final long serialVersionUID = -42665250230498099L;

    @NotNull(message = "roleId不为空")
    private Long roleId;

}
