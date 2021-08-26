package io.shulie.takin.web.biz.pojo.response.linkmanage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/16 4:34 下午
 */
@Data
public class MiddleWareResponse {

    @JsonProperty("package")
    private String libName;

    @JsonProperty("pluginType")
    private String pluginType;

    @JsonProperty("pluginName")
    private String pluginName;

    @JsonProperty("status")
    private SupportStatusResponse statusResponse;

}
