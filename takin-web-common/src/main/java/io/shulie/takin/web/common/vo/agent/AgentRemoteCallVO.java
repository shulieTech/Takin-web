package io.shulie.takin.web.common.vo.agent;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/6/4 4:16 下午
 */
@Data
public class AgentRemoteCallVO {
    // 全局黑名单 已废弃
    @JsonProperty("bLists")
    private List<Blacklist> bLists;
    // 接口信息
    @JsonProperty("wLists")
    private List<RemoteCall> wLists;
    // 新版黑名单数据
    @JsonProperty("newBlists")
    private List<AgentBlacklistVO> newBlists;

    @Data
    public static class Blacklist {
        @JsonProperty("REDIS_KEY")
        private String REDIS_KEY;
    }

    @Data
    public static class RemoteCall {
        @JsonProperty("INTERFACE_NAME")
        private String INTERFACE_NAME;
        // 废弃
        @JsonProperty("appNames")
        private List<String> appNames;
        // 废弃
        @JsonProperty("isGlobal")
        private Boolean isGlobal;
        @JsonProperty("TYPE")
        private String TYPE;
        /**
         * 职责
         * mock
         * forward
         * white
         */
        @JsonProperty("checkType")
        private String checkType;
        @JsonProperty("content")
        private String content;
        @JsonProperty("forwardUrl")
        private String forwardUrl;
    }

}


