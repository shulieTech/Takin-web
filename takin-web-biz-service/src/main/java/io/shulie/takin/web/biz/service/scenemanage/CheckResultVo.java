package io.shulie.takin.web.biz.service.scenemanage;

import java.util.Collection;

import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckResultVo {

    private String sceneName;
    private Integer podNumber;
    private Integer status;
    private String resourceId;
    private Long reportId;
    private Collection<CheckResult> checkList;
}
