package io.shulie.takin.web.biz.convert.db.parser;

import java.util.List;
import java.util.Map;

import com.pamirs.attach.plugin.dynamic.one.Converter;
import com.pamirs.attach.plugin.dynamic.one.template.Template;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.convert.db.parser.style.StyleTemplate;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;

/**
 * @Author: 南风
 * @Date: 2021/9/6 10:30 下午
 */
public abstract class AbstractTemplateParser implements TemplateParser, StyleTemplate {

    /**
     * 将影子数据映射成模板数据
     *
     * @param recordId
     * @return
     */
    @Override
    public ShadowDetailResponse convertDetailByTemplate(Long recordId,String appName) {
        return null;
    }

    /**
     * 将影子方案信息按对应模版解析
     *
     * @param dsType
     * @return
     */
    @Override
    public abstract List<? extends StyleTemplate> convertShadowMsgWithTemplate(Integer dsType, Boolean isNewData, String cacheType, Converter.TemplateConverter.TemplateEnum templateEnum,ShadowTemplateSelect select);

    /**
     * 删除记录
     *
     * @param recordId
     */
    @Override
    public abstract void deletedRecord(Long recordId);

    @Override
    public abstract void enable(Long recordId, Integer status);

    public List<SelectVO> queryCacheType() {
        return null;
    }

    @Override
    public Converter.TemplateConverter.TemplateEnum convert(String connPoolName) {
        for (Converter.TemplateConverter.TemplateEnum templateEnum : Converter.TemplateConverter.TemplateEnum.values()) {

            try {
                Object t = templateEnum.getaClass().newInstance();
                if (Template.class.isAssignableFrom(t
                        .getClass())) {
                    Template template = (Template) t;
                    if (connPoolName.equals( template.getName())) {
                        return templateEnum;
                    }

                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<String, String> convertModel() {
        return null;
    }
}
