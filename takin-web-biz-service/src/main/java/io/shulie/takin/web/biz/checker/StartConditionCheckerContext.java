package io.shulie.takin.web.biz.checker;

import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StartConditionCheckerContext {

    private Long sceneId;
    private String resourceId;
    private Long taskId;
    private Long reportId;
    private Long tenantId;
    // 压力机集群
    private String machineId;
    private Integer machineType;
    private String attachId;
    private SceneManageWrapperOutput sceneData;
    private SceneTaskStartInput input;
    private SceneManageWrapperDTO sceneDataDTO;
    private boolean initTaskAndReport;

    // 临时记录错误信息使用
    private boolean isInspect; // 是否巡检
    private String message;
    private String uniqueKey;
    private long time = System.currentTimeMillis();

    /**
     * 是否定时
     */
    private Boolean isTiming = false;

    private StartConditionCheckerContext(Long sceneId) {
        this.sceneId = sceneId;
    }

    public static StartConditionCheckerContext of(Long sceneId) {
        return new StartConditionCheckerContext(sceneId);
    }
}
