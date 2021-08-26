package io.shulie.takin.web.data.param.scriptmanage;

import java.util.Date;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/11 2:54 下午
 */
@Data
public class ScriptExecuteResultCreateParam {

    /**
     * 实例id
     */
    private Long scripDeployId;

    /**
     * 脚本id
     */
    private Long scriptId;

    /**
     * 脚本id
     */
    private Integer scriptVersion;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 执行人
     */
    private String Executor;

    /**
     * 执行结果
     */
    private Boolean success;

    /**
     * 执行结果
     */
    private String result;

}
