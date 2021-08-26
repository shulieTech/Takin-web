package io.shulie.takin.web.biz.pojo.response.application;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.common.vo.application.AppRemoteCallVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/29 12:18 上午
 */
@Data
public class AppRemoteCallResponse extends AppRemoteCallVO {
    @ApiModelProperty(name = "id", value = "id")
    private Long id;

    /**
     * 前端显示接口类型
     */
    @ApiModelProperty(name = "interfaceTypeSelectVO", value = "前端显示接口类型")
    private SelectVO interfaceTypeSelectVO;

    /**
     * 前端显示配置类型
     */
    @ApiModelProperty(name = "typeSelectVO", value = "前端显示配置类型")
    private SelectVO typeSelectVO;
}
