package io.shulie.takin.web.biz.pojo.request.pts;

import lombok.Data;

import java.io.Serializable;

@Data
public class PtsCsvLineRequest implements Serializable {

    private Integer lineNumber;

    private String paramsName;
}
