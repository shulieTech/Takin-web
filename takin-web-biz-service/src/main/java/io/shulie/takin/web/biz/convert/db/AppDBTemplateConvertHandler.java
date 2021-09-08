package io.shulie.takin.web.biz.convert.db;

import com.pamirs.attach.plugin.dynamic.Converter;
import com.pamirs.attach.plugin.dynamic.template.Template;
import com.pamirs.pradar.logger.LoggerFactory;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.biz.convert.db.parser.C3p0TemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.DruidTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.HikariTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.JedisClusterTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.JedisSentinelTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.JedisSingleTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.LettuceClusterTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.LettuceSentinelTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.LettuceSingleTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.RedissionClusterTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.RedissionSentinelTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.RedissionSingleTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.RedissonMasterSlaveTemplateParser;
import io.shulie.takin.web.biz.convert.db.parser.TemplateParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Author: 南风
 * @Date: 2021/8/30 3:44 下午
 */
@Component
public class AppDBTemplateConvertHandler {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(AppDBTemplateConvertHandler.class);


    /**
     * key：TemplateParser#supportClass 的全路径
     * value：TemplateParser具体实现
     */
    private static final Map<String, TemplateParser> PARSER_HOLDER = new HashMap<>(32);

    private static final List<Class<? extends TemplateParser>> PARSER_CLASS_HOLDER = new ArrayList<>(16);

    static {

        PARSER_CLASS_HOLDER.add(C3p0TemplateParser.class);
        PARSER_CLASS_HOLDER.add(DruidTemplateParser.class);
        PARSER_CLASS_HOLDER.add(HikariTemplateParser.class);
        PARSER_CLASS_HOLDER.add(JedisClusterTemplateParser.class);
        PARSER_CLASS_HOLDER.add(JedisSentinelTemplateParser.class);
        PARSER_CLASS_HOLDER.add(JedisSingleTemplateParser.class);
        PARSER_CLASS_HOLDER.add(LettuceClusterTemplateParser.class);
        PARSER_CLASS_HOLDER.add(LettuceSentinelTemplateParser.class);
        PARSER_CLASS_HOLDER.add(LettuceSingleTemplateParser.class);
        PARSER_CLASS_HOLDER.add(RedissionClusterTemplateParser.class);
        PARSER_CLASS_HOLDER.add(RedissionSentinelTemplateParser.class);
        PARSER_CLASS_HOLDER.add(RedissionSingleTemplateParser.class);
        PARSER_CLASS_HOLDER.add(RedissonMasterSlaveTemplateParser.class);

        for (Class<? extends TemplateParser> parser : PARSER_CLASS_HOLDER) {
            try {
                TemplateParser templateParser = parser.newInstance();
                PARSER_HOLDER.put(templateParser.getSupperClass().getName(), templateParser);
            } catch (Exception ignore) {
            }
        }
        PARSER_CLASS_HOLDER.clear();
    }

    public static <T extends Template> T analysisTemplateData(AppShadowDatabaseDTO dto) {
        Converter.TemplateConverter.TemplateEnum templateEnum = Converter.TemplateConverter.ofKey(dto.getType());
        if (templateEnum != Converter.TemplateConverter.TemplateEnum._default) {
            String className = templateEnum.getaClass().getName();
            if (PARSER_HOLDER.containsKey(className)) {
                return PARSER_HOLDER.get(className).convertTemplate(dto);
            }

        }
        return null;
    }

}
