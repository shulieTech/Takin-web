package io.shulie.takin.web.app.conf.mybatis;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.shulie.takin.utils.security.MD5Utils;
import io.shulie.takin.web.app.conf.mybatis.datasign.SignCommonUtil;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author fanxx
 * @date 2020/11/4 11:02 上午
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    @SneakyThrows
    @Override
    public void insertFill(MetaObject metaObject) {
        // 判断下 环境字段是否存在
        if (metaObject.hasSetter(TenantField.FIELD_TENANT_ID.getFieldName())) {
            this.strictInsertFill(metaObject, TenantField.FIELD_TENANT_ID.getFieldName(), Long.class, WebPluginUtils.traceTenantId());
        }

        if (metaObject.hasSetter(TenantField.FIELD_ENV_CODE.getFieldName())) {
            this.strictInsertFill(metaObject, TenantField.FIELD_ENV_CODE.getFieldName(), String.class, WebPluginUtils.traceEnvCode());
        }
        if (metaObject.hasSetter(TenantField.FIELD_USER_ID.getFieldName())) {
            this.strictInsertFill(metaObject, TenantField.FIELD_USER_ID.getFieldName(), Long.class, WebPluginUtils.traceUserId());
        }

        if (metaObject.hasSetter(TenantField.FIELD_TENANT_APP_KEY.getFieldName())) {
            this.strictInsertFill(metaObject, TenantField.FIELD_TENANT_APP_KEY.getFieldName(), String.class, WebPluginUtils.traceTenantAppKey());
        }

        Class<?> clz = metaObject.getOriginalObject().getClass();
        if (clz.isAnnotationPresent(EnableSign.class)) {
            log.info("【sign operation】insert SQL 开始执行签名计算");
            Map<String, Integer> signKeyOrderMap = new HashMap<>();
            Map<String, String> signKeyValueMap = new LinkedHashMap<>();
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(SignField.class)) {
                    SignField signField = field.getAnnotation(SignField.class);
                    signKeyOrderMap.put(field.getName(), signField.order());
                }
            }
            String signStr = "";
            if (!signKeyOrderMap.isEmpty()) {
                signKeyOrderMap = MapUtil.sortByValue(signKeyOrderMap, false);
                for (String key : signKeyOrderMap.keySet()) {
                    Object fieldValByName = getFieldValByName(key, metaObject);
                    {
                        signKeyValueMap.put(key, String.valueOf(fieldValByName));
                    }
                }
                 signStr = MD5Utils.getInstance().getMD5(signKeyValueMap.toString());
            }
            this.strictInsertFill(metaObject, SignCommonUtil.SIGN_FIELD, String.class, signStr);
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }


}
