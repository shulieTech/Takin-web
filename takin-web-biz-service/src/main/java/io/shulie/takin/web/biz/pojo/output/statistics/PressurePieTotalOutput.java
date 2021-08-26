package io.shulie.takin.web.biz.pojo.output.statistics;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/11/30 9:16 下午
 */
@Data
public class PressurePieTotalOutput {
    private List<PressurePieTotal> data;
    private Integer total;
    @Data
    public static class PressurePieTotal {
        private String type;
        private Integer value;
        public PressurePieTotal() {}
        public PressurePieTotal(String type, Integer value) {
            this.type = type;
            this.value = value;
        }
    }
}
