package io.shulie.takin.web.biz.pojo.output.probe;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author liuchuan
 * @date 2021/5/11 1:54 下午
 */
@Data
@ApiModel("出参类-探针列表出参")
public class ProbeListOutput {

    @ApiModelProperty("探针记录 id")
    private Long id;

    @ApiModelProperty("版本")
    private String version;

}
