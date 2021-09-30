package io.shulie.takin.web.app.conf;

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
        this.strictInsertFill(metaObject, "customerId", Long.class, WebPluginUtils.getTenantId());
        this.strictInsertFill(metaObject, "userId", Long.class, WebPluginUtils.getUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
