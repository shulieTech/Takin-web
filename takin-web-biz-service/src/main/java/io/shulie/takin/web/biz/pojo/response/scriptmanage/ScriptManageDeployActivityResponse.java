package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageDeployActivityResponse implements Serializable {
    private static final long serialVersionUID = 4125650713571865983L;

    /**
     * 脚本发布id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;
}
