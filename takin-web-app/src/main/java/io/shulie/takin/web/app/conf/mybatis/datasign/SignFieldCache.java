package io.shulie.takin.web.app.conf.mybatis.datasign;

import cn.hutool.core.map.MapUtil;
import io.shulie.takin.web.data.annocation.SignField;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 南风
 * @Date: 2022/3/2 2:18 下午
 * 缓存签名字段
 */
@Component
public class SignFieldCache {

    private static SignFieldCache cache;

    private static final Map<String, List<Field>> SIGN_FIELD_MAP = new HashMap<>();

    public static String SIGN_FIELD = "sign";



    public static SignFieldCache getInstance(){
        if (cache == null){
            cache = new SignFieldCache();
        }
        return cache;
    }


    /**
     * 添加缓存
     * @param clz
     */
    private  void setSignFields(Class<?> clz){

        Field[] fields = clz.getDeclaredFields();
        Map<Field, Integer> keySortMap = new HashMap<>();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            if (field.isAnnotationPresent(SignField.class)) {
                SignField signField = field.getAnnotation(SignField.class);
                keySortMap.put(field, signField.order());
            }

            if(SIGN_FIELD.equals(field.getName())){
                keySortMap.put(field,1000);
            }
        }

        keySortMap = MapUtil.sortByValue(keySortMap, false);
        SIGN_FIELD_MAP.put(clz.getName(), new ArrayList<>(keySortMap.keySet()));
    }



    /**
     * 取出缓存
     * @param clz
     * @return
     */
    public   List<Field> getSignFields(Class<?> clz){
        if(this.getSignFields(clz.getName()).size() <=0){
            this.setSignFields(clz);
        }
        return this.getSignFields(clz.getName());
    }

    /**
     * 取出缓存
     * @param className
     * @return
     */
    public List<Field> getSignFields(String className){
        if(!SIGN_FIELD_MAP.containsKey(className)){
            return new ArrayList<>();
        }
        return SIGN_FIELD_MAP.get(className);
    }

}
