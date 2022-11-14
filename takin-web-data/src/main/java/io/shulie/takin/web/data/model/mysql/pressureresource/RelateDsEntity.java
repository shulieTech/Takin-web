package io.shulie.takin.web.data.model.mysql.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/31 6:41 PM
 */
@Data
public class RelateDsEntity extends PressureResourceRelateDsEntityV2 {
    @ApiModelProperty("中间件名称 druid, hikari,c3p0等")
    private String middlewareName;

    @ApiModelProperty("中间件类型 缓存/连接池")
    private String middlewareType;

    @ApiModelProperty("影子数据源")
    private String shadowDatabase;

    @ApiModelProperty("影子数据源用户名")
    private String shadowUserName;

    @ApiModelProperty("影子数据源密码")
    private String shadowPassword;
}
