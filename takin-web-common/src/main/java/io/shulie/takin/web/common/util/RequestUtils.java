package io.shulie.takin.web.common.util;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.util
 * @ClassName: RequestUtils
 * @Description: TODO
 * @Date: 2022/1/14 10:23
 */
public class RequestUtils {
    /**
     * special char handle
     * '：用于包裹搜索条件，需转为\'
     * %：用于代替任意数目的任意字符，需转换为\%
     * _：用于代替一个任意字符，需转换为\_
     * \：转义符号，需转换为\\在java中\也是特殊字符因此需要两次转义，而replace正则需要再次转义
     *
     * \要最先处理，防止把转义\再次转一遍
     * eg:abc _'%*sdf\\
     * result:abc\_\'\%\*sdf\\\\
     *
     * author :bamboo
     * url:https://editor.csdn.net/md?not_checkout=1&articleId=111865685
     */
    public static String escapeSqlSpecialChar(String str ){
        return  str.trim().replaceAll("\\s", "").replace("\\", "\\\\\\\\")
            .replace("_", "\\_").replace("\'", "\\'")
            .replace("%", "\\%").replace("*", "\\*");
    }
}
