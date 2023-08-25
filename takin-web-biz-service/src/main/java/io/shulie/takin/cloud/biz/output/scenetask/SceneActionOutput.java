package io.shulie.takin.cloud.biz.output.scenetask;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import lombok.Data;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/11/13 上午10:58
 */
@Data
public class SceneActionOutput {

    private Long data;

    private Long reportId;

    private List<String> msg;

    private SceneManageWrapperOutput sceneData;
}
