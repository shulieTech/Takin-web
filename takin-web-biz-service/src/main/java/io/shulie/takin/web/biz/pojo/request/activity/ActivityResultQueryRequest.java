package io.shulie.takin.web.biz.pojo.request.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 根据条件查询业务活动
 * @author zhaoyong
 */
@Data
@ApiModel("业务活动查询对象")
public class ActivityResultQueryRequest implements Serializable {

    @ApiModelProperty("应用名")
    @NotNull(message = "应用名不能为空")
    private String applicationName;

    @ApiModelProperty("入口entrance")
    @NotNull(message = "入口entrance不能为空")
    private String entrancePath;
}
