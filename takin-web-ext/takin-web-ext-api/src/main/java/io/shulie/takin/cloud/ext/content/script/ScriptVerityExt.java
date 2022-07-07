package io.shulie.takin.cloud.ext.content.script;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptVerityExt {

    /**
     * 业务请求列表
     */
    private List<String> request;

    /**
     * 脚本路径
     */
    private String scriptPath;
    /**
     * 脚本版本(管理版本)
     * <p>混合压测前是0</p>
     * <p>混合压测后是1</p>
     */
    private Integer version;

    private boolean useNewVerify;
    // 脚本文件路径, 此处是单个
    private List<String> scriptPaths = new ArrayList<>();
    // 插件路径，多个按逗号分隔
    private List<String> pluginPaths = new ArrayList<>();
    // 数据文件路径，多个按逗号分隔
    private List<String> csvPaths = new ArrayList<>();
    // 附件文件路径，多个按逗号分隔
    private List<String> attachments = new ArrayList<>();
}
