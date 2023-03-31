package io.shulie.takin.web.biz.pojo.response.pts;

import lombok.Data;

import java.io.Serializable;

@Data
public class PtsAssertResponse implements Serializable {

    private String assertName;

    private Boolean success;

    private String failureMessage;
}
