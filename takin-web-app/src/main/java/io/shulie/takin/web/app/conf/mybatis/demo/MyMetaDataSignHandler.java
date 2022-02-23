package io.shulie.takin.web.app.conf.mybatis.demo;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SignUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.shulie.takin.web.data.annocation.SignAllow;
import io.shulie.takin.web.data.annocation.SignField;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 南风
 * @Date: 2022/2/22 5:25 下午
 */
@Slf4j
@Component
public class MyMetaDataSignHandler implements MetaObjectHandler {

    public static String privateKey = "1545345";

    @Override
    public void insertFill(MetaObject metaObject) {
        Class<?> clz = metaObject.getOriginalObject().getClass();
        if(clz.isAnnotationPresent(SignAllow.class)){
            Map<String,Integer> signKeyOrderMap = new HashMap<>();
            Map<String,String> signKeyValueMap = new HashMap<>();
            Field[] fields = clz.getDeclaredFields();
            for(Field field : fields){
                if(field.isAnnotationPresent(SignField.class)){
                    SignField signField = field.getAnnotation(SignField.class);
                    signKeyOrderMap.put(field.getName(),signField.order());
                }
            }
            signKeyOrderMap = MapUtil.sortByValue(signKeyOrderMap, false);
            for (String key : signKeyOrderMap.keySet()) {
                String value =String.valueOf(getFieldValByName(key, metaObject));
                signKeyValueMap.put(key,value);
            }

            String signStr = SignUtil.signParamsMd5(signKeyValueMap, privateKey);
            this.strictInsertFill(metaObject, "sign", String.class,signStr);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
