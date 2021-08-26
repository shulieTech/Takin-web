package io.shulie.takin.web.common.vo.agent;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/8 7:23 下午
 */
@Data
public class AgentBlacklistVO {
    @JsonProperty("appName")
    private String appName;
    @JsonProperty("blacklists")
    private List<String> blacklists;
}
