package io.shulie.takin.web.biz.pojo.response.dashboard;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "快捷入口")
public class QuickAccessResponse extends UserCommonExt {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "入口名称")
    private String quickName;
    @ApiModelProperty(value = "logo地址")
    private String quickLogo;
    @ApiModelProperty(value = "接口地址")
    private String urlAddress;
    @ApiModelProperty(value = "顺序")
    private Integer order;
}
