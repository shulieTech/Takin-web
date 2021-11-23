package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/11/23
 */
@Data
public class ApplicationPluginUpgradeHistoryResponse {

    @ApiModelProperty(value = "升级批次")
    private String upgradeBatch;

    @ApiModelProperty(value = "状态  0 未升级 1升级成功 2升级失败 3已回滚 4升级中")
    private Integer status;

    @ApiModelProperty(value = "应用数")
    private Integer appCount;

    @ApiModelProperty(value = "失败详情")
    private List<UpgradeErrorInfo> errorDetails;

    public ApplicationPluginUpgradeHistoryResponse(String upgradeBatch, Integer status, Integer appCount) {
        this.upgradeBatch = upgradeBatch;
        this.status = status;
        this.appCount = appCount;
    }

    public ApplicationPluginUpgradeHistoryResponse() {
    }

    @Data
    public static class UpgradeErrorInfo {
        @ApiModelProperty(value = "应用名")
        private String applicationName;
        @ApiModelProperty(value = "失败原因")
        private String errorMsg;

        public UpgradeErrorInfo(String applicationName, String errorMsg) {
            this.applicationName = applicationName;
            this.errorMsg = errorMsg;
        }

        public UpgradeErrorInfo() {
        }
    }
}
