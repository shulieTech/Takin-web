package io.shulie.takin.adapter.cloud.convert;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneBusinessActivityRefInput;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneBusinessActivityRefOpen;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author mubai
 * @date 2020-10-29 15:20
 */
public class SceneBusinessActivityRefInputConvert {

    public static SceneBusinessActivityRefInput of(SceneBusinessActivityRefOpen open) {
        return BeanUtil.copyProperties(open, SceneBusinessActivityRefInput.class);
    }

    public static List<SceneBusinessActivityRefInput> ofLists(List<SceneBusinessActivityRefOpen> list) {
        List<SceneBusinessActivityRefInput> outs = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return outs;
        }
        list.forEach(open -> outs.add(of(open)));
        return outs;
    }
}
