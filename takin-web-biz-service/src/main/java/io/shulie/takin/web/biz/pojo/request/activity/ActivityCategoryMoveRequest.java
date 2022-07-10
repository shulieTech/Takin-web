package io.shulie.takin.web.biz.pojo.request.activity;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除业务活动分类")
public class ActivityCategoryMoveRequest {

    @NotNull(message = "源业务活动分类Id不能为空")
    @ApiModelProperty("源业务活动分类Id")
    private Long from;

    @NotNull(message = "目标业务活动分类Id不能为空")
    @ApiModelProperty("目标业务活动分类Id")
    private Long to;

}
