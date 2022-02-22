package io.shulie.takin.web.common.vo.agent;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO.Blacklist;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/6/4 4:16 下午
 */
@Data
public class OldAgentRemoteCallVO {
    @JsonProperty("bLists")
    private List<Blacklist> bLists;
    // 接口信息
    @JsonProperty("wLists")
    private List<RemoteCall> wLists;
    // 新版黑名单数据
    @JsonProperty("newBlists")
    private List<AgentBlacklistVO> newBlists;

    @Data
    public static class RemoteCall {
        @JsonProperty("INTERFACE_NAME")
        private String INTERFACE_NAME;
        @JsonProperty("appNames")
        private List<String> appNames;
        @JsonProperty("isGlobal")
        private Boolean isGlobal;
        @JsonProperty("TYPE")
        private String TYPE;
    }

}


