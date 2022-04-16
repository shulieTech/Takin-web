package io.shulie.takin.cloud.common.script.jmeter;

import java.util.List;

import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import org.dom4j.Document;

/**
 * @author HengYu
 * @date 2021/4/12 4:02 下午
 */
public abstract class JmxParser {

    /**
     * 获取Jmeter 脚本请求入口
     *
     * @param document       JMX对应文档对象
     * @param content        脚本原文
     * @param scriptParseExt 脚本解析拓展  (pt 数量对象)
     * @return -
     */
    abstract List<ScriptUrlExt> getEntryContent(Document document, String content, ScriptParseExt scriptParseExt);
}
