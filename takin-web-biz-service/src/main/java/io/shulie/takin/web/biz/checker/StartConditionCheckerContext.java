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
    private SceneManageWrapperOutput sceneData;
    private SceneTaskStartInput input;
    private SceneManageWrapperDTO sceneDataDTO;
    private boolean initTaskAndReport;

    // 临时记录错误信息使用
    private String message;
    private String uniqueKey;
    private long time = System.currentTimeMillis();

    private StartConditionCheckerContext(Long sceneId) {
        this.sceneId = sceneId;
    }

    public static StartConditionCheckerContext of(Long sceneId) {
        return new StartConditionCheckerContext(sceneId);
    }
}
