package io.shulie.takin.web.biz.pojo.response.statistics;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/11/30 9:16 下午
 */
@Data
public class PressurePieTotalResponse {
    @ApiModelProperty(value = "分类统计")
    private List<PressurePieTotal> data;
    @ApiModelProperty(value = "合计")
    private Integer total;
    @Data
    public static class PressurePieTotal {
        private String type;
        private Integer value;
    }
}
