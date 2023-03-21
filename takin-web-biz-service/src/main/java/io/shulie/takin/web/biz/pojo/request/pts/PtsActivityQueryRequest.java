package io.shulie.takin.web.biz.pojo.request.pts;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsActivityQueryRequest
 * @Description
 * @createTime 2023年03月15日 17:13
 */
@Data
@ApiModel("pts查询业务活动列表")
public class PtsActivityQueryRequest extends PagingDevice {

    @ApiModelProperty("业务活动名称")
    private String activityName;

    @ApiModelProperty("入口信息")
    private String entrance;
}
