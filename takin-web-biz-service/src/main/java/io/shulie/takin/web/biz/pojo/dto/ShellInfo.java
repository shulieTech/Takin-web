package io.shulie.takin.web.biz.pojo.dto;

import lombok.Data;

import java.util.Map;

/**
 * ClassName:    ShellInfo
 * Package:    io.shulie.takin.web.biz.pojo.dto
 * Description:
 * Datetime:    2022/7/11   23:07
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class ShellInfo {
    //是否ssh
    private boolean ssh;
    //是否免密
    private boolean noPass;
    //是否交互模式
    private boolean spawn;
    //具体执行的命令
    private String cmd;
    //ip地址
    private String ip;
    //用户名称
    private String name;
    //用户密码
    private String password;
    //交互参数
    private Map<String, String> expectMap;
}
