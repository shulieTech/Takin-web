package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/21 下午5:01
 */
@Data
public class SceneManageSceneIdVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = 5258828941952507100L;

    private Long sceneId;
}
