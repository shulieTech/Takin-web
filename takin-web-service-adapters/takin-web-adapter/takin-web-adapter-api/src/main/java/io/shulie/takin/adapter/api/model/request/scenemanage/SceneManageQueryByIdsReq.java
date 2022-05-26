package io.shulie.takin.adapter.api.model.request.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mubai
 * @date 2020-11-16 19:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryByIdsReq extends ContextExt {

    private List<Long> sceneIds;
}
