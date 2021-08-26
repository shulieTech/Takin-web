package com.pamirs.takin.common.util;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 说明: 菜鸟阿斯旺/Prada接口请求参数封装
 *
 * @author shulie
 * @version v1.0
 * @date 2019/3/4 11:23
 * @see com.pamirs.takin.common.util.RequestPradaUtilTest
 */
public class RequestPradarUtil {

    /**
     * pradar同步mysql接口数据url
     */
    final public static String PRADA_SYNCHRONIZED_MYSQL_URL = "/AppUrlInfo";
    /**
     * pradar获取app_name地址
     */
    final public static String PRADA_APP_NAME_URL = "/pradarapp/source/queryAppNames.do";
    /**
     * pradar 获取 影子表配置
     */
    final public static String PRADAR_GET_SHADOW_CONFIG_URL = "/PamirsDatabaseInfo";
    /**
     * 阿斯旺APP_KEY
     */
    final private static String APP_KEY_SPT = "sto-full-link";
    /**
     * 阿斯旺SECRET_KEY
     */
    final private static String SECRET_KEY_SPT = "123456";
    /**
     * prada APP_KEY
     */
    final private static String APP_KEY_PRADA = "pradar";
    /**
     * prada SECRET_KEY
     */
    final private static String SECRET_KEY_PRADA = "pradar-takin";

    /**
     * 初始化SPT请求参数
     *
     * @return 包含请求token的map集合
     * @author shulie
     * @date 2018年5月21日
     */
    public static Map<String, Object> initSPT() {
        Map<String, Object> parameters = new HashMap<>(10);
        String timestamp = "" + System.currentTimeMillis();
        String token = MD5Util.getMD5(APP_KEY_SPT + SECRET_KEY_SPT + timestamp);
        parameters.put("appKey", APP_KEY_SPT);
        parameters.put("token", token);
        parameters.put("timestamp", timestamp);
        return parameters;
    }

    /**
     * 说明: 初始化prada请求参数
     *
     * @return 包含请求token的map集合
     * @author shulie
     * @date 2019/3/4 11:16
     */
    public static Map<String, Object> initPrada() {
        Map<String, Object> parameters = new HashMap<>(10);
        String timestamp = "" + System.currentTimeMillis();
        String token = MD5Util.getMD5("python_takin" + "python" + timestamp);
        parameters.put("appKey", "python_takin");
        parameters.put("token", token);
        parameters.put("timestamp", timestamp);
        return parameters;
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }
}
