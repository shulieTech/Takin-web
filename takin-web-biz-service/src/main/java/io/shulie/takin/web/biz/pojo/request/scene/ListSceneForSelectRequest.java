package io.shulie.takin.web.biz.pojo.request.scene;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author liuchuan
 * @date 2021/6/8 3:05 下午
 */
@Data
@ApiModel("入参类 --> 下拉框场景列表入参类")
public class ListSceneForSelectRequest {

    @ApiModelProperty(value = "场景状态, 1 压测中", required = true)
    @NotNull(message = "场景状态" + AppConstants.MUST_BE_NOT_NULL)
    @Range(min = 1, max = 1, message = "场景状态当前只能传压测中!")
    private Integer status;

}
