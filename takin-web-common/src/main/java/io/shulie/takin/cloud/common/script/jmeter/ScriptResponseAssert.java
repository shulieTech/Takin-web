package io.shulie.takin.cloud.common.script.jmeter;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScriptResponseAssert implements Serializable {

    private String testStrings;

    private String testField;

    private String testType;
}
