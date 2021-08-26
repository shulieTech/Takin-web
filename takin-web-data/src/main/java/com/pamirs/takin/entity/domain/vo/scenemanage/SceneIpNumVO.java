package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/15 下午3:28
 */
@Data
public class SceneIpNumVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = 5601318389362884272L;

    private Integer concurrenceNum;
}
