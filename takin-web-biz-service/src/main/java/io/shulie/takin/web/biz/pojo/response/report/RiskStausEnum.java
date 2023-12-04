package io.shulie.takin.web.biz.pojo.response.report;

/**
 * @author zhangz
 * Created on 2023/12/4 11:39
 * Email: zz052831@163.com
 */

public enum RiskStausEnum {
    INIT(0), FAILED(1), SUCCESS(2);
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static RiskStausEnum getRiskStausEnum(int status) {
        for (RiskStausEnum riskStausEnum : RiskStausEnum.values()) {
            if (riskStausEnum.getStatus() == status) {
                return riskStausEnum;
            }
        }
        return null;
    }

    RiskStausEnum(int status) {
        this.status = status;
    }
}
