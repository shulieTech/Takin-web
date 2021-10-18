package io.shulie.takin.web.app.conf.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/11/4 11:02 上午
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {



    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, TenantField.FIELD_TENANT_ID, Long.class, WebPluginUtils.traceTenantId());
        // 判断下 环境字段是否存在
        if (metaObject.hasSetter(TenantField.FIELD_ENV_CODE)) {
            this.strictInsertFill(metaObject, TenantField.FIELD_ENV_CODE, String.class, WebPluginUtils.traceEnvCode());
        }
        if (metaObject.hasSetter(TenantField.FIELD_USER_ID)) {
            this.strictInsertFill(metaObject, TenantField.FIELD_USER_ID, Long.class, WebPluginUtils.traceUserId());
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
