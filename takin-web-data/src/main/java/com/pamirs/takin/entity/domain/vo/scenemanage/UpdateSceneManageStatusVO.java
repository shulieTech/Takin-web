package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/18 下午6:24
 */
@Data
public class UpdateSceneManageStatusVO implements Serializable {

    private Long id;

    private Integer preStatus;

    private Integer afterStatus;
}
