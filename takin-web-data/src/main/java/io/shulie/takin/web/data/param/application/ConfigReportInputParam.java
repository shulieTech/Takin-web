package io.shulie.takin.web.data.param.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/9/28 4:56 下午
 */
@ApiModel(value = "ConfigReportInputParam", description = "agent配置上报数据接受类")
@Data
public class ConfigReportInputParam implements Serializable {

    @ApiModelProperty(name = "applicationName", value = "应用名称",required = true)
    private String applicationName;

    @ApiModelProperty(name = "agentId", value = "agentId",required = true)
    private  String agentId;

    @ApiModelProperty(name = "globalConf", value = "全局配置")
    private List<ConfigInfo> globalConf;

    @ApiModelProperty(name = "appConf", value = "应用级配置")
    private List<ConfigInfo> appConf;

    @Data
    @ApiModel(value = "configInfo", description = "配置明细数据")
    public static class ConfigInfo {

        @ApiModelProperty(name = "key", value = "配置key")
        private String key;

        @ApiModelProperty(name = "value", value = "value")
        private String value;

        @ApiModelProperty(name = "parentKey", value = "父级配置key")
        private String parentKey;

        @ApiModelProperty(name = "bizType", value = "业务类型 0:静默配置")
        private Integer bizType;
    }
}
