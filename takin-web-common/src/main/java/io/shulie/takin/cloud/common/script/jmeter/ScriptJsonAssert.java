package io.shulie.takin.cloud.common.script.jmeter;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScriptJsonAssert implements Serializable {

    private String jsonPath;

    private String expectedValue;

    private Boolean jsonvalidation;
}
