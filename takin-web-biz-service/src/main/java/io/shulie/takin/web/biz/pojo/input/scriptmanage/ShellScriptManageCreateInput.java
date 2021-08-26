package io.shulie.takin.web.biz.pojo.input.scriptmanage;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/8 4:28 下午
 */
@Data
public class ShellScriptManageCreateInput {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 脚本类型;0为jmeter脚本,1为shell脚本
     */
    private Integer type;

    /**
     * shell脚本内容
     */
    private String content;

    /**
     * 文件类型
     */
    private Integer fileType;


}
