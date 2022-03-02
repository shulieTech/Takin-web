package io.shulie.takin.web.app.conf.mybatis.datasign;

import cn.hutool.crypto.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 南风
 * @Date: 2022/3/2 2:52 下午
 */
@Slf4j
@Component
public class SignCommonUtil {

    public static String SIGN_FIELD = "sign";

    //todo demo写死，后面替换
    public static String privateKey = "1545345";

    public static boolean checkSignData(Class<?> clz, List<?> result) throws IllegalAccessException {

        SignFieldCache signFieldCache = new SignFieldCache();

        List<Field> signFields = signFieldCache.getSignFields(clz);
        Map<String, String> signMap = new LinkedHashMap<>();

        String oldSign = "";
        boolean check = true;
        for (int i = 0; i < result.size(); i++) {
            for (Field field : signFields) {

                if (SIGN_FIELD.equals(field.getName())) {
                    oldSign = String.valueOf(field.get(result.get(i)));
                }else{
                    signMap.put(field.getName(), String.valueOf(field.get(result.get(i))));

                }
            }
            //验签
            String newSign = SignUtil.signParamsMd5(signMap, privateKey);
            if (!oldSign.equals(newSign)) {
                log.error("【sign operation fail】select SQL 第【{}】行数据 签名验证失败", i + 1);
                check = false;
                break;
            }
        }
        return check;
    }

    public static  List<Field> getSignFields(Class<?> clz){
        SignFieldCache signFieldCache = new SignFieldCache();
        return  signFieldCache.getSignFields(clz);
    }
}
