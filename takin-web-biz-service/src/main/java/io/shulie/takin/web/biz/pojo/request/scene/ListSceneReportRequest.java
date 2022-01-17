package io.shulie.takin.web.biz.pojo.request.scene;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author liuchuan
 * @date 2021/6/8 3:05 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参类 --> 场景运行的报告列表入参类")
@ToString(callSuper = true)
public class ListSceneReportRequest extends PageBaseDTO {

    @ApiModelProperty(value = "场景ids", required = true)
    @NotEmpty(message = "场景ids" + AppConstants.MUST_BE_NOT_NULL)
    private List<Long> sceneIds;

    @ApiModelProperty(value = "排序类型, 不传, 0, 按照创建时间倒序, 1, tps倒序, 2, tps正序, 默认 0")
    private Integer orderType = 0;

}
