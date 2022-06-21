package io.shulie.takin.adapter.api.model.request.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景列表查询
 *
 * @author qianshui
 * @date 2020/4/17 下午2:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryRequest extends ContextExt {

    private Long sceneId;

    private String sceneName;

    private Integer status;

}
