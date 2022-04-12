package io.shulie.takin.web.data.param.report;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportActivityInterfaceQueryParam extends PagingDevice {

    private Long reportId;
    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式
     */
    private String sortType;

    /**
     * 入口
     */
    public List<EntranceParam> entrances;

    public String getSortField() {
        if (StringUtils.isBlank(sortField)) {
            return "";
        }
        return sortField;
    }

    public String getSortType() {
        if (StringUtils.isBlank(sortType)) {
            return "";
        }
        return sortType;
    }

    @Data
    @EqualsAndHashCode
    public static class EntranceParam {
        private String appName;
        private String serviceName;
        private String methodName;
        private String rpcType;
    }
}
