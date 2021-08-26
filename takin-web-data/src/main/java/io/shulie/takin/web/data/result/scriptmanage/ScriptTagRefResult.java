package io.shulie.takin.web.data.result.scriptmanage;

import java.util.Date;

import lombok.Data;


/**
 * @author zhaoyong
 */
@Data
public class ScriptTagRefResult {

    private Long id;

    /**
     * 场景id
     */
    private Long scriptId;

    /**
     * 标签id
     */
    private Long tagId;

    private Date gmtCreate;

    private Date gmtUpdate;

}
