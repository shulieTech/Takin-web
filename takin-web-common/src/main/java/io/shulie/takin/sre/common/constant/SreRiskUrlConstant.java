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
    String GET_REPORT_RISK_DIAGNOSIS_CONFIRM_URL = "/takin-sre/api/risk/pressure/clear/data/check";
    String GET_REPORT_RISK_DIAGNOSIS_DELETE_URL = "/takin-sre/api/risk/pressure/clear/data";
    String SET_SRE_CONFIG_SLA = "/takin-sre/api/risk/pressure/config/sla";
    String SET_SRE_CONFIG_CHAIN = "/takin-sre/api/risk/pressure/config/chain";
    String GET_SRE_SLA_PARAMS_FROM_COLLECTOR = "/api/clickhouse/getSlaParams";
    String COLLECTOR_SYNC_TRACE = "/api/clickhouse/syncTrace";

    String GET_REPORT_RISK_ITEM_CONDITION_URL = "/takin-sre/api/risk/extraction/risk/item/";

    String GET_RISK_ITEM_APP_RATE="/takin-sre/api/risk/pressure/diagnosis/order";
}
