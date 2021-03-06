package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author mubai
 * @date 2020-11-12 14:08
 */

@Data
@ApiModel(value = "内存图表返回数据")
public class MemoryAnalysisResponse implements Serializable {

    private static final long serialVersionUID = -5427941161243522078L;

    @ApiModelProperty(name = "heapMemory",value = "堆栈信息")
    private List<MemoryModelVo> heapMemory ;

    @ApiModelProperty(name = "gcCount",value = "gc次数")
    private List<MemoryModelVo> gcCount;

    @ApiModelProperty(name = "gcCost",value = "gc耗时")
    private List<MemoryModelVo> gcCost;
}
