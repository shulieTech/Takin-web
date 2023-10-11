package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import lombok.Data;

@Data
public class ScriptCsvVariableJsonPathDTO {
    /**
     * 变量
     */
    private String variable;
    /**
     * jsonpath 对应的key
     */
    private String jsonPathKey;
}
