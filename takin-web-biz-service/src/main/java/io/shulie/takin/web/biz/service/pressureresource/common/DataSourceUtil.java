package io.shulie.takin.web.biz.service.pressureresource.common;

import com.pamirs.takin.common.util.MD5Util;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/7 3:56 PM
 */
public class DataSourceUtil {
    public static String generateDsKey_ext(String businessDatabase, String userName) {
        String key = String.format("%s-%s",
                businessDatabase, userName);
        return MD5Util.getMD5(key);
    }

}
