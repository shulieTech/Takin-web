package io.shulie.takin.web.biz.pojo.output.scriptmanage.shell;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/11 2:54 下午
 */
@Data
public class ScriptExecuteOutput {

    private Long id;
    /**
     * 实例id
     */
    private Long scripDeployId;

    private Integer scriptVersion;

    /**
     * 创建时间
     */
    private String gmtCreate;

    /**
     * 执行人
     */
    private String executor;

    /**
     * 执行结果
     */
    private Boolean success;

    /**
     * 是否停止
     */
    private Boolean isStop;

    /**
     * 执行结果
     */
    private String result;

}
