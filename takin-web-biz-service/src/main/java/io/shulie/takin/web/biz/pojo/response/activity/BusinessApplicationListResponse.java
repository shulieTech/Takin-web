package io.shulie.takin.web.biz.pojo.response.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 1:54 下午
 */
@Data
@ApiModel("出参类-业务活动的应用列表出参")
public class BusinessApplicationListResponse {

    @ApiModelProperty("应用id, 字符串形式, 防止前端忽略")
    private String applicationId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    private Integer accessStatus;
}
