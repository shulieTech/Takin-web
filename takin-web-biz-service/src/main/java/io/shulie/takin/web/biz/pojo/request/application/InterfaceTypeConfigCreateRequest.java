package io.shulie.takin.web.biz.pojo.request.application;

import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@ApiModel("中间件类型-配置类型关系 request")
public class InterfaceTypeConfigCreateRequest {

    /**
     * 接口类型Id
     */
    private String childTypeName;

    /**
     * 支持配置
     */
    private String configName;

    /**
     * 支持配置文本
     */
    private String configText;

    /**
     * 备注
     */
    private String remark;

    public void checkRequired() {
        if (StringUtils.isBlank(getChildTypeName()) || getConfigName() == null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, "childTypeName、configName 不能为空");
        }
    }
}
