package io.shulie.takin.web.data.model.mysql.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 扩展类,主要是旧表的字段信息，需要放到新表展示
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/10/31 6:52 PM
 */
@Data
public class RelateRemoteCallEntity extends PressureResourceRelateRemoteCallEntityV2 {
    @ApiModelProperty("配置类型0:未配置,1:白名单配置,2:返回值mock,3:转发mock")
    private Integer type;

    @ApiModelProperty("服务端应用名")
    private String serverAppName;
}
