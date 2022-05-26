package io.shulie.takin.cloud.data.result.scenemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新对应脚本时, 场景列表
 * 放弃使用
 *
 * @author liuchuan
 */
@Data
@Deprecated
@EqualsAndHashCode(callSuper = true)
public class SceneManageListFromUpdateScriptResult extends ContextExt {

    /**
     * 场景id
     */
    private Long id;

    /**
     * 扩展字段
     */
    private String features;
}
