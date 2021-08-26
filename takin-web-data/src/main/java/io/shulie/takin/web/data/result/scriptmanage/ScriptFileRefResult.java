package io.shulie.takin.web.data.result.scriptmanage;

import java.util.Date;

import lombok.Data;

/**
 * @author shulie
 */
@Data
public class ScriptFileRefResult {
    private Long id;

    /**
     * 文件id
     */
    private Long fileId;

    /**
     * 脚本id
     */
    private Long scriptDeployId;

    private Date gmtCreate;

    private Date gmtUpdate;
}
