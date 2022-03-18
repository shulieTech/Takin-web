package io.shulie.takin.web.biz.pojo.request.application;

import java.util.Objects;

import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@ApiModel("中间件小类型 request")
public class InterfaceTypeChildCreateRequest {

    /**
     * 主类型名称
     */
    @ApiModelProperty("主类型名称")
    private String mainName;

    /**
     * 中间件中文描述
     */
    @ApiModelProperty("中间件中文描述")
    private String name;

    /**
     * 中间件英文名称
     */
    @ApiModelProperty("中间件英文名称")
    private String engName;

    public void checkRequired() {
        if (StringUtils.isAnyBlank(getName(), getEngName()) || Objects.isNull(getMainName())) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "name、engName、mainName 不能为空");
        }
    }
}
