package io.shulie.takin.web.common.enums.opsscript;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpsScriptExecutionEnum {

    PEDDING(0, "待执行"),
    RUNNING(1, "执行中"),
    FINISH(2, "执行结束");
    private Integer status;
    private String name;

    public static String getNameByStatus(Integer status) {
        for (OpsScriptExecutionEnum statusEnum : OpsScriptExecutionEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum.getName();
            }
        }
        return "";
    }

}
