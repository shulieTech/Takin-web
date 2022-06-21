package io.shulie.takin.cloud.ext.content.script;

import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptParseExt {

    /**
     * 携带的压测标数量
     */
    private Integer ptSize = 0;

    /**
     * 脚本中有效的请求
     */
    private List<ScriptUrlExt> requestUrl;
    /**
     * 文件内容
     */
    private String content;

}
