package io.shulie.takin.web.biz.convert.db.parser;

import com.alibaba.fastjson.JSONObject;
import com.pamirs.attach.plugin.dynamic.template.RedisTemplate;
import com.pamirs.attach.plugin.dynamic.template.Template;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;

/**
 * @Author: 南风
 * @Date: 2021/8/30 5:01 下午
 * 暂时不区分
 */
@Deprecated
public class JedisSingleTemplateParser extends AbstractTemplateParser{

    /**
     * 返回对应的模板class
     *
     * @return
     */
    @Override
    public Class<?>  getSupperClass() {
        return  RedisTemplate.JedisSingleTemplate.class;
    }


    /**
     * 返回解析后的模板对象
     *
     * @param dto
     * @return
     */
    @Override
    public <T extends Template> T convertTemplate(AppShadowDatabaseDTO dto) {
        String attachment = dto.getAttachment();
        RedisTemplate.JedisSingleTemplate jedisSentinelTemplate = JSONObject.parseObject(attachment, RedisTemplate.JedisSingleTemplate.class);
        return (T) jedisSentinelTemplate;
    }
}
