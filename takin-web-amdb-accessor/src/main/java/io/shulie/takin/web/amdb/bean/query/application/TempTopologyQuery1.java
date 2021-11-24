package io.shulie.takin.web.amdb.bean.query.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TempTopologyQuery1 {
    private String inAppName;
    private String inService;
    private String inMethod;
    private String startTime;
    private String endTime;
    private int timeGap;
}
