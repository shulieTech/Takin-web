package io.shulie.takin.web.biz.pojo.output.application;

import lombok.Data;

@Data
public class ApplicationEsDetailOutput extends ApplicationDsDetailOutput{

    private String businessNodes;

    private String performanceTestNodes;
}
