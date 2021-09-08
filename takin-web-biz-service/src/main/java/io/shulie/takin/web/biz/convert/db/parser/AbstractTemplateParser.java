package io.shulie.takin.web.biz.convert.db.parser;

import com.pamirs.attach.plugin.dynamic.template.AbstractTemplate;
import com.pamirs.attach.plugin.dynamic.template.Template;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.data.model.mysql.NewBaseEntity;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/9/6 10:30 下午
 */
public abstract class AbstractTemplateParser implements TemplateParser{

    /**
     * 返回对应的模板对象
     *
     * @return
     */
    public abstract Class<?> getSupperClass();

    /**
     * 返回解析后的模板对象
     *
     * @param dto
     * @return
     */
    public abstract  <T extends Template> T convertTemplate(AppShadowDatabaseDTO dto);


    /**
     * 将影子数据映射成模板数据
     * @param recordId
     * @return
     */
    public ShadowDetailResponse convertDetailByTemplate(Long recordId) {
        return null;
    }

    /**
     * 获取protect属性集合
     * @param cla
     * @return
     */
    public  List<String> getProtectArray(Class<? extends AbstractTemplate> cla) {
        Field[] f = cla.getDeclaredFields();
        List<String> protectList = Lists.newArrayList();
        for (int i = 0; i < f.length; i++) {
            if (Modifier.isProtected(f[i].getModifiers())) {
                protectList.add(f[i].getName());
            }
        }
        return protectList;
    }
}
