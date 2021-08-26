package io.shulie.takin.web.biz.pojo.output.scriptmanage.shell;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/11 10:51 上午
 */
@Data
public class ShellScriptManageExecuteOutput {
    private Boolean success;
    private Boolean isStop;
    private List<String> message;
}
