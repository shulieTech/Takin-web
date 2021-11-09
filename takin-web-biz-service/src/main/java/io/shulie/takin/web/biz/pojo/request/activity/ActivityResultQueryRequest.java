package io.shulie.takin.web.biz.pojo.request.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 根据条件查询业务活动
 * @author zhaoyong
 */
@Data
@ApiModel("业务活动查询对象")
public class ActivityResultQueryRequest implements Serializable {

    @ApiModelProperty("应用名")
    private String applicationName;

    @ApiModelProperty("入口entrance")
    private String entrance;
}
