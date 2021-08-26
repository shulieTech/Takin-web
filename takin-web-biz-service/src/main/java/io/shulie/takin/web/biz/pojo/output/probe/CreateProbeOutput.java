package io.shulie.takin.web.biz.pojo.output.probe;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author liuchuan
 * @date 2021/6/11 1:33 下午
 */
@Data
@ApiModel("出参类-探针创建出参")
public class CreateProbeOutput {

    @ApiModelProperty("探针记录 id")
    private Long probeId;

    @ApiModelProperty("探针 version")
    private String probeVersion;

}
