package io.shulie.takin.adapter.api.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportActivityResp extends ContextExt {

    private Long sceneId;

    private String sceneName;

    private Long reportId;

    private List<BusinessActivity> businessActivityList;

    @Data
    public static class BusinessActivity {
        String activityName;
        String bindRef;
    }
}