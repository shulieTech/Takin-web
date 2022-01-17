package io.shulie.takin.web.biz.pojo.output.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/11 1:33 下午
 */
@Data
@ApiModel("出参类-下拉框场景列表出参")
public class SceneListForSelectOutput {

    @ApiModelProperty("场景id")
    private Long id;

    @ApiModelProperty("场景名称")
    private String name;

}
