package io.shulie.takin.web.biz.convert.db.parser;

import com.pamirs.attach.plugin.dynamic.one.Converter;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.convert.db.parser.style.StyleTemplate;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;

import java.util.List;
import java.util.Map;

/**
 * @Author: 南风
 * @Date: 2021/8/30 4:16 下午
 */
public interface TemplateParser {
    /**
     * 将影子方案信息按对应模版解析
     * @param dsType,protectArray
     * @return
     */
    List<? extends StyleTemplate> convertShadowMsgWithTemplate(Integer dsType, Boolean isNewData, String cacheType, Converter.TemplateConverter.TemplateEnum templateEnum);


    ShadowDetailResponse convertDetailByTemplate(Long recordId);


    /**
     * 删除记录
     * @param recordId
     */
     void deletedRecord(Long recordId);

     void enable(Long recordId,Integer status);

    List<SelectVO> queryCacheType();

    Converter.TemplateConverter.TemplateEnum convert(String connPoolName);

     Map<String, String> convertModel();

}
