package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/12 下午3:47
 */
@Data
public class SceneIdVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = -9013282538536892377L;

    private Long sceneId;
}
