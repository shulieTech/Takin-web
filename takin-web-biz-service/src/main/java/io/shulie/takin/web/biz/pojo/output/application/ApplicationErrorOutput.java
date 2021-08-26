package io.shulie.takin.web.biz.pojo.output.application;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fanxx
 * @date 2020/10/16 11:36 上午
 */
public class ApplicationErrorOutput {

    @JsonProperty("id")
    private String exceptionId;

    @JsonProperty("agentIds")
    private List<String> agentIdList;

    @JsonProperty("description")
    private String description;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("time")
    private String time;

    public String getExceptionId() {
        return exceptionId;
    }

    public ApplicationErrorOutput setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
        return this;
    }

    public List<String> getAgentIdList() {
        return agentIdList;
    }

    public ApplicationErrorOutput setAgentIdList(List<String> agentIdList) {
        this.agentIdList = agentIdList;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApplicationErrorOutput setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public ApplicationErrorOutput setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ApplicationErrorOutput setTime(String time) {
        this.time = time;
        return this;
    }
}
