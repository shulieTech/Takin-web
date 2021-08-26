package io.shulie.takin.web.biz.pojo.request.scene;

import java.util.List;

import lombok.Data;

/**
 * 调用Cloud启动压测场景
 *
 * @author 张天赐
 */
@Data
public class StartRequest {

    private Long sceneId;

    private Boolean leakSqlEnable;

    private List<Long> enginePluginIds;

    private Boolean continueRead = false;
}
