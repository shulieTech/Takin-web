package io.shulie.takin.web.amdb.bean.query.report;

import java.util.List;

import io.shulie.amdb.common.request.AbstractAmdbBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportQueryDTO extends AbstractAmdbBaseRequest {

    private String reportId;
    private List<InterfaceParam> params;

    public ReportQueryDTO(String reportId) {
        this.reportId = reportId;
        setTenantAppKey(AbstractAmdbBaseRequest.DEFAULT_TENANT_KEY);
        setEnvCode(AbstractAmdbBaseRequest.DEFAULT_ENV_CODE);
    }

    @Data
    public static class InterfaceParam {
        private String entranceAppName;
        private String entranceServiceName;
        private String entranceMethodName;
        private String entranceRpcType;
        private String appName;
        private String serviceName;
        private String methodName;
        private String rpcType;
    }

}
