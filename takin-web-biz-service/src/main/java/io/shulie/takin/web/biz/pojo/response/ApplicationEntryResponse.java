package io.shulie.takin.web.biz.pojo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 1:54 下午
 */
@Data
@ApiModel("出参类-应用入口出参")
public class ApplicationEntryResponse {

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("http 为入口, /user/xxx, kafka 为 group")
    private String serviceName;

    @ApiModelProperty("http 为请求方式, get, kafka 为, kafka 为 topic")
    private String methodName;

    @ApiModelProperty("1 是, 虚拟活动, 0 否, 正常活动")
    private Integer isVirtual;

}
