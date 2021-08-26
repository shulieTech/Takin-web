package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/15 下午4:13
 */
@Data
public class SceneParseVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = 33734315777916535L;

    private Long scriptId;

    private String uploadPath;

    private Boolean absolutePath;
}
