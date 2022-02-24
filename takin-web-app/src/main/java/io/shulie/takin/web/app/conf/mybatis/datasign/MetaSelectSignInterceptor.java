package io.shulie.takin.web.app.conf.mybatis.datasign;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SignUtil;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: 南风
 * @Date: 2022/2/23
 */
@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class))
@Component
@AllArgsConstructor
@Slf4j
public class MetaSelectSignInterceptor implements Interceptor {

    public static String privateKey = "1545345";

    public static String SIGN_FIELD = "sign";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String oldSign = "";
        boolean sign = true;
        List result = (List) invocation.proceed();
        if (result.isEmpty()) return result;
        Class<?> clz = result.get(0).getClass();
        if (clz.isAnnotationPresent(EnableSign.class)) {
            log.info("【sign operation】select SQL 开始执行签名计算");
            Map<String, Integer> keySortMap = new HashMap<>();
            Map<String, String> signMap = new HashMap<>();
            Field[] fields = clz.getDeclaredFields();
            for(int i=0;i< result.size();i++){
                for (Field field : fields) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    if (field.isAnnotationPresent(SignField.class)) {
                        SignField signField = field.getAnnotation(SignField.class);
                        keySortMap.put(field.getName(), signField.order());
                        signMap.put(field.getName(), String.valueOf(field.get(result.get(i))));
                    }

                    if (SIGN_FIELD.equals(field.getName())) {
                        oldSign = String.valueOf(field.get(result.get(i)));
                    }
                }

                //验签
                keySortMap = MapUtil.sortByValue(keySortMap, false);
                Map<String, String> signMapSort = new HashMap<>();
                for (String param : keySortMap.keySet()) {
                    signMapSort.put(param, signMap.get(param));
                }
                String newSign = SignUtil.signParamsMd5(signMapSort, privateKey);
                if (!oldSign.equals(newSign)) {
                    log.info("【sign operation fail】select SQL 第【{}】行数据 签名验证失败",i+1);
                    sign = false;
                    break;
                }
            }

            if(!sign){
                log.info("【sign operation】select SQL 签名验证失败");
                return new ArrayList<>();
            }
            log.info("【sign operation】select SQL 签名验证成功");
            return result;
        }
        return result;

    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
