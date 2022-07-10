package io.shulie.takin.web.biz.pojo.request.activity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("创建业务活动分类")
public class ActivityCategoryCreateRequest {

    @NotNull(message = "业务活动分类父级Id不能为空")
    @ApiModelProperty("业务活动分类父级Id")
    private Long parentId;

    @NotBlank(message = "业务活动分类名称不能为空")
    @ApiModelProperty("业务活动分类名称")
    private String title;

}
