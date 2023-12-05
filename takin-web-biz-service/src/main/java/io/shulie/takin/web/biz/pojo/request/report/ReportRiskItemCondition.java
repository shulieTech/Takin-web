package io.shulie.takin.web.biz.pojo.request.report;

import lombok.Data;

/**
 * @author zhangz
 * Created on 2023/12/5 16:16
 * Email: zz052831@163.com
 */

@Data
public class ReportRiskItemCondition {
    private ConditionEnum condition;
    private String date;

    public enum ConditionEnum {
        APP("app"), SERVICE("service"), SLA("sla");
        private String value;

        ConditionEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
