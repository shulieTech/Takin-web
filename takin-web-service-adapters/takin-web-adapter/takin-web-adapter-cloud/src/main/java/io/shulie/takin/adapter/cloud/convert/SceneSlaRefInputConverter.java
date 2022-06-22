package io.shulie.takin.adapter.cloud.convert;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneSlaRefOpen;

/**
 * @author mubai
 * @date 2020-10-29 10:56
 */

public class SceneSlaRefInputConverter {

    public static SceneSlaRefInput of(SceneSlaRefOpen sceneSlaRef) {
        return BeanUtil.copyProperties(sceneSlaRef, SceneSlaRefInput.class);
    }

    public static List<SceneSlaRefInput> ofList(List<SceneSlaRefOpen> sceneSlaRefs) {
        List<SceneSlaRefInput> result = new ArrayList<>();
        sceneSlaRefs.forEach(sceneSlaRef -> result.add(of(sceneSlaRef)));
        return result;
    }

}
