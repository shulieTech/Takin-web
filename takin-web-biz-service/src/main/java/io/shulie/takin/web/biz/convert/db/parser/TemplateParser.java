package io.shulie.takin.web.biz.convert.db.parser;

import com.pamirs.attach.plugin.dynamic.template.AbstractTemplate;
import com.pamirs.attach.plugin.dynamic.template.Template;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.data.model.mysql.NewBaseEntity;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/8/30 4:16 下午
 */
public interface TemplateParser {


    /**
     * 返回对应的模板对象
     * @return
     */
    Class<?> getSupperClass();

    /**
     * 返回解析后的模板对象
     * @param dto
     * @param <T>
     * @return
     */
    <T extends Template>T convertTemplate(AppShadowDatabaseDTO dto);


    ShadowDetailResponse convertDetailByTemplate(Long recordId);

    /**
     * 获取protect属性集合
     * @param T
     * @return
     */
    List<String> getProtectArray(Class<? extends AbstractTemplate> T);





}
