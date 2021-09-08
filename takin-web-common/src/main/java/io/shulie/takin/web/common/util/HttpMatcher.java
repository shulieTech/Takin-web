package io.shulie.takin.web.common.util;

import io.shulie.takin.utils.string.StringUtil;
import org.springframework.util.AntPathMatcher;

/**
 * @Author: 南风
 * @Date: 2021/8/26 11:34 上午
 * http url 匹配
 */
public class HttpMatcher {
    public HttpMatcher() {
    }

    private String format(String url) {
        /**
         * 确保首位是/
         */
        if (url.charAt(0) != '/') {
            url = '/' + url;
        }

        /**
         * 确保长度大于1的末尾不是/
         */
        if (url.length() > 1 && url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }


    public boolean match(String url, String expression) {
        if (StringUtil.isEmpty(url)) {
            return false;
        }
        if (StringUtil.isEmpty(expression)) {
            return false;
        }
        url = format(url.trim());
        expression = format(expression.trim());
        if (url.equals(expression)) {
            return true;
        }
        if (expression.split("/").length != url.split("/").length) {
            return false;
        }
        String res = url;
        try {
            StringBuilder pre = new StringBuilder();
            int index = url.indexOf("://");
            if (index > -1) {
                pre = pre.append(url.substring(0, index + 3));
                url = url.substring(index + 3, url.length());
                if (url.indexOf("/") > -1) {
                    pre = pre.append(url.substring(0, url.indexOf("/")));
                    url = url.substring(url.indexOf("/"), url.length());
                }
            }


            String[] sourceSplit = url.split("/");
            Boolean flag = true;
            String temp = "";

            String[] paramArr = expression.split("\\{");
            //规则中含有多个参数或者只有一个参数的规则
            if (paramArr.length >= 3 || (paramArr.length == 2 && "/".equals(paramArr[0]) && paramArr[1].contains("}"))) {
                String[] apiSplit = expression.split("/");
                //如果规则长度和源字符不相等,进入下一条规则匹配
                if (apiSplit.length != sourceSplit.length) {
                    return false;
                }
                //如果长度相等
                //忽略第一个空值,从第二位开始匹配
                int paramCount = 0;
                for (int i = 1; i < apiSplit.length; i++) {
                    String word = apiSplit[i];
                    //如果是变量,跳过
                    if ("{".equals(word.substring(0, 1)) && "}".equals(word.substring(word.length() - 1))) {
                        paramCount++;
                        continue;
                    }
                    //如果两者不相等,直接进入下一个规则匹配
                    if (!word.equals(sourceSplit[i])) {
                        return false;
                    }
                }
                //如果等值匹配上,则返回规则
                if (flag) {
                    //如果是全参数匹配,需要继续向下检索,是否存在等值匹配
                    if (paramCount == apiSplit.length - 1) {
                        //保存临时结果集
                        temp = expression;
                        return false;
                    }
                    return true;
                }
            }
            // /app/add/{name}
            // /app/add/1

            // /app/{name}/add
            // /app/1/add
            if (expression.contains("/{") && expression.contains("}")) {
                String subApi = expression.substring(0, expression.indexOf("/{"));
                //如果包含前缀
                if (!StringUtil.isEmpty(subApi) && res.startsWith(subApi)) {
                    //获取后缀
                    String suffixApi = expression.substring(expression.indexOf("}") + 1);
                    String tempStr = null;
                    if (suffixApi.length() != 0) {
                        tempStr = res.substring(0 + subApi.length() + 1);
                    } else {
                        //如果后缀为空,需要继续比较位数
                        if (expression.split("/").length != res.split("/").length)
                            return false;
                    }

                    // add rules:如果属于格式2 同时匹配后缀api
                    if (!(tempStr == null ? true :
                            (!tempStr.contains("/") ? false :
                                    tempStr.substring(tempStr.indexOf("/")).equals(suffixApi)))) {
                        return false;
                    }
                    return true;
                }
            }
            if (!StringUtil.isEmpty(temp)) {
                return true;
            }
        } catch (Throwable t) {
        }
        return false;
    }


    public static void main(String[] args) {
        HttpMatcher httpMatcher = new HttpMatcher();


        AntPathMatcher matcher = new AntPathMatcher();
        String url = "/api/demo/{id}/{bookId}";
        String expression = "/api/demo/{id}/{bookId}";
        boolean match = matcher.match(expression, url);
        boolean match2 = httpMatcher.match(url, expression);
        System.out.println(match);
        System.out.println(match2);


    }
}
