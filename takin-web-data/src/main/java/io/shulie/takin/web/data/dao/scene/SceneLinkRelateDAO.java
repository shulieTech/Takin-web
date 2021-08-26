package io.shulie.takin.web.data.dao.scene;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;

/**
 * @author 无涯
 * @date 2021/5/28 5:38 下午
 */
public interface SceneLinkRelateDAO extends IService<SceneLinkRelateEntity> {

    /**
     * 获取关联
     * @param param
     * @return
     */
    List<SceneLinkRelateResult> getList(SceneLinkRelateParam param);
}
