package io.shulie.takin.web.data.result.scriptmanage;

import java.util.Date;
import java.util.List;

import lombok.Data;


/**
 * @author zhaoyong
 */
@Data
public class ScriptManageResult {

    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;

    /**
     * 脚本版本
     */
    private Integer scriptVersion;

    private Integer isDeleted;

    /**
     * 拓展字段
     */
    private String feature;

    /**
     * 脚本发布id
     */
    private List<ScriptManageDeployResult> scriptManageDeployResultList;

}
