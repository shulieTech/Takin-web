package io.shulie.takin.sre.common.constant;

/**
 * @author zhangz
 * Created on 2023/12/4 20:56
 * Email: zz052831@163.com
 */

public interface SreRiskUrlConstant {
    String GET_REPORT_RISK_ITEM_PAGES_URL = "/takin-sre/api/risk/pressure/diagnosis/query";
    String GET_REPORT_RISK_DIAGNOSIS_URL = "/takin-sre/api/risk/pressure/diagnosis/status";
    String REPORT_RISK_DIAGNOSIS_URL = "/takin-sre/api/risk/pressure/diagnosis";
    String GET_REPORT_RISK_DIAGNOSIS_CONFIRM_URL = "/takin-sre/api/risk/pressure/diagnosis/delete/confirm";
    String GET_REPORT_RISK_DIAGNOSIS_DELETE_URL = "/takin-sre/api/risk/pressure/diagnosis/delete";
}
