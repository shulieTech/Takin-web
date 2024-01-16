package io.shulie.takin.web.biz.pojo.response.linkmanage;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScriptNodeParsedResponse implements Serializable {

    private Boolean jmxCheckSuccess = true;

    private String jmxCheckErrorMsg;
}
