package io.shulie.takin.web.biz.pojo.request.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Data
@ApiModel("业务活动查询对象")
public class ActivityQueryRequest extends PagingDevice {

    @ApiModelProperty("业务活动名称")
    private String activityName;

    @ApiModelProperty("业务域")
    private String domain;

    @ApiModelProperty("是否变更，0正常，1变更")
    @JsonProperty("ischange")
    private Integer isChange;

    @ApiModelProperty(name = "link_level", value = "业务活动等级")
    private String linkLevel;
}
