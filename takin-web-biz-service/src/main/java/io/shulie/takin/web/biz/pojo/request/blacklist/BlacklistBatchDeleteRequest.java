package io.shulie.takin.web.biz.pojo.request.blacklist;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/6/9 下午5:22
 */
@Data
public class BlacklistBatchDeleteRequest {
    @ApiModelProperty(name = "blistIds", value = "黑名单ad")
    @NotNull
    private List<Long> ids;
}
