package io.shulie.takin.cloud.data.param.report;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/17 3:34 下午
 */
@Data
public class ReportQueryParam {
    private String endTime;
    /**
     * 状态:0就绪，1生成中，2已完成
     */
    private Integer status;
    /**
     * 是否已删除：0正常，1已删除
     */
    private Integer isDel;
    // 是否jobId不为空,true-加上notnull条件，false-不加条件
    private boolean jobIdNotNull;

    /**
     * 压测类型
     */
    private PressureTypeRelation pressureTypeRelation;

    @Data
    public static class PressureTypeRelation {
        private Integer pressureType;
        private Boolean have;//关系 包含=true 不包含=false

        private PressureTypeRelation() {

        }

        private PressureTypeRelation(Integer pressureType) {
            this.pressureType = pressureType;
        }

        private PressureTypeRelation(Boolean have) {
            this.have = have;
        }

        public PressureTypeRelation(Integer pressureType, Boolean have) {
            this.pressureType = pressureType;
            this.have = have;
        }
    }
}
