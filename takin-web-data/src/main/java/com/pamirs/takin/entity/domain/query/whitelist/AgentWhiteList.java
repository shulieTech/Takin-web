package com.pamirs.takin.entity.domain.query.whitelist;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AgentWhiteList {

    @JsonProperty("INTERFACE_NAME")
    private String interfaceName;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("SOURCETYPE")
    private String sourceType;

    @JsonProperty("WLISTID")
    private Long wlistId;


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AgentWhiteList that = (AgentWhiteList)o;
        return Objects.equals(interfaceName, that.interfaceName) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interfaceName, type);
    }
}
