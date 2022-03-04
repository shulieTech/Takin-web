package io.shulie.takin.web.biz.pojo.request.application;

import java.util.Objects;

import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@ApiModel("中间件大类型 request")
public class InterfaceTypeMainCreateRequest {

    /**
     * 中间件中文描述
     */
    @ApiModelProperty("中间件中文描述")
    private String name;

    /**
     * 中间件英文名称：不能为空
     */
    @ApiModelProperty("中间件英文名称")
    private String engName;

    /**
     * 对应大数据rpcType
     */
    @ApiModelProperty("amdb rpcType")
    private Integer rpcType;

    /**
     * valueOrder
     */
    @ApiModelProperty("valueOrder")
    private Integer valueOrder;

    public void checkRequired() {
        if (StringUtils.isAnyBlank(getName(), getEngName()) || Objects.isNull(getValueOrder()) || Objects.isNull(getRpcType())) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "name、engName、valueOrder、rpcType 不能为空");
        }
    }
}
