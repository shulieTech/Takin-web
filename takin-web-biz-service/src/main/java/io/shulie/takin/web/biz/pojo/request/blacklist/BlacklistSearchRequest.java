package io.shulie.takin.web.biz.pojo.request.blacklist;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.shulie.takin.web.common.annocation.Trimmed;
import io.shulie.takin.web.common.annocation.Trimmed.TrimmerType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 2:22 下午
 */
@Data
public class BlacklistSearchRequest extends PagingDevice {
    /**
     * 黑名单类型
     */
    @ApiModelProperty(name = "type", value = "黑名单类型")
    private Integer type ;

    /**
     * redisKey
     */
    @ApiModelProperty(name = "redisKey", value = "redisKey")
    @Trimmed(value = TrimmerType.SIMPLE)
    private String redisKey ;


    /**
     * 应用id
     */
    @ApiModelProperty(name = "applicationId", value = "应用id")
    private Long applicationId ;

}
