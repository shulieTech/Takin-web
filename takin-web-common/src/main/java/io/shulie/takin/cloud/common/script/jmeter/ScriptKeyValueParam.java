package io.shulie.takin.cloud.common.script.jmeter;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScriptKeyValueParam implements Serializable {

    private String key;

    private String value;
}
