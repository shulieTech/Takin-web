package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-17 09:53
 */

@Data
@ApiModel(value = "压力机修改入参莫模型")
public class PressureMachineUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1921691612428565110L;

    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private String flag;

    /**
     * 网络带宽总大小
     * 通过前端编辑
     */
    @ApiModelProperty(value = "网络带宽总代小")
    private Long transmittedTotal;

}
