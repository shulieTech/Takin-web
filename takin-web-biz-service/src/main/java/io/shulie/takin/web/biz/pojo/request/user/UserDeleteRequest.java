package io.shulie.takin.web.biz.pojo.request.user;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangXT
 * @date 2020/11/4 14:58
 */
@Data
@ApiModel(value = "UserDeleteRequest", description = "删除用户入参")
public class UserDeleteRequest {
    @ApiModelProperty(name = "userIdList", value = "用户id")
    @NotEmpty
    private List<Long> userIdList;
}
