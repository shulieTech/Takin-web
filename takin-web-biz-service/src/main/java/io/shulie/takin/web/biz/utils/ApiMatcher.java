package io.shulie.takin.web.biz.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author hezhongqi
 * @Package io.shulie.takin.web.biz.utils
 * @ClassName: ApiMatcher
 * @description:
 * @date 2023/10/10 10:55
 */
public class ApiMatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiMatcher.class);

    private static  final Pattern placeholderPattern = Pattern.compile("\\{([^}]+)}");

    public static Integer isRestful(String url) {
        java.util.regex.Matcher matcher = placeholderPattern.matcher(url);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }


    public static String replaceAll(String url, String data) {
        return url.replaceAll("\\{([^}]+)}",data);
    }

    private static Matcher matcher = null;


    public static void setConfigRestfulExpression(String pattern) {
        LOGGER.info("set config restful expression : {}", pattern);
        matcher = new Matcher(pattern);
    }
    private static List<Object> getMatch(Matcher matcher, String lookupPath) {
        return matcher.match(lookupPath);
    }

    public static List<Object> getRestfulValue(String lookupPath) {
        return getMatch(matcher, lookupPath);
    }



    private static class Matcher {



        private final String pattern;

        private final int placeholderCount;

        private String[] slashSegments;

        private boolean[] braceStart;
        private boolean[] braceEnd;
        private int slashSegmentsLength;
        private boolean braceMatch;
        private boolean mixExpressionMatch;
        private String subApi;
        private int subApiLength;
        private String suffixApi;
        private boolean isBlankSubApi;
        private boolean isBlankSuffixApi;
        /**
         * 是否是全匹配
         */
        private boolean matchesAll;

        private Matcher(String pattern) {
            java.util.regex.Matcher matcher = placeholderPattern.matcher(pattern);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            this.placeholderCount = count;
            if (count > 0) {
                pattern = matcher.replaceAll("{value}");
            }
            this.pattern = pattern;
            final int endIndex = pattern.indexOf("/{");
            if (endIndex < 0) {
                matchesAll = true;
            } else {
                this.slashSegments = StringUtils.split(pattern, '/');
                this.slashSegmentsLength = this.slashSegments.length;
                String[] braceSegments = StringUtils.split(pattern, '{');
                this.braceMatch = braceSegments.length >= 3 || (braceSegments.length == 2 && "/".equals(
                        braceSegments[0]) && braceSegments[1].contains("}"));
                this.mixExpressionMatch = pattern.contains("/{") && pattern.contains("}");
                this.subApi = pattern.substring(0, endIndex);
                this.subApiLength = subApi.length();
                this.isBlankSubApi = StringUtils.isBlank(subApi);
                this.suffixApi = pattern.substring(pattern.indexOf("}") + 1);
                this.isBlankSuffixApi = StringUtils.isBlank(suffixApi);
                this.braceStart = new boolean[this.slashSegmentsLength];
                this.braceEnd = new boolean[this.slashSegmentsLength];
                for (int i = 0; i < slashSegmentsLength; i++) {
                    String word = slashSegments[i];
                    this.braceStart[i] = word.charAt(0) == '{';
                    this.braceEnd[i] = word.charAt(word.length() - 1) == '}';
                }
            }
        }

        private String format(String url) {
            // 去除？后面的部分
            if (url.contains("?")) {
                url = url.split("\\?")[0];
            }

            if (url.startsWith("http://")) {
                url = url.substring(7);
            } else if (url.startsWith("https://")) {
                url = url.substring(8);
            }
            int index = url.indexOf("/");
            if (index != -1) {
                url = url.substring(index);
            }
            /*
              确保首位是/
             */
            if (url.charAt(0) != '/') {
                url = '/' + url;
            }

            /*
              确保长度大于1的末尾不是/
             */
            if (url.length() > 1 && url.charAt(url.length() - 1) == '/') {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }

        public List<Object> match(String url) {
            List<Object> values = Lists.newArrayList();
            if (StringUtils.isBlank(url)) {
                return Lists.newArrayList();
            }
            url = format(url.trim());
            // 判断个数是否匹配
            if (slashSegmentsLength != StringUtils.split(url, '/').length) {
                return Lists.newArrayList();
            }
            try {
                String[] sourceSplit = StringUtils.split(url, '/');
                String temp = "";

                //规则中含有多个参数或者只有一个参数的规则
                if (braceMatch) {
                    //如果规则长度和源字符不相等,进入下一条规则匹配
                    if (slashSegmentsLength != sourceSplit.length) {
                        return Lists.newArrayList();
                    }
                    //如果长度相等
                    //忽略第一个空值,从第二位开始匹配
                    int paramCount = 0;
                    int wordCount = 0;
                    for (int i = 0, len = slashSegmentsLength; i < len; i++) {
                        String word = slashSegments[i];
                        //如果是变量,跳过
                        if (braceStart[i] && braceEnd[i]) {
                            values.add(sourceSplit[i]);
                            paramCount++;
                            continue;
                        }
                        //如果两者不相等,直接进入下一个规则匹配
                        if (!word.equals(sourceSplit[i])) {
                            return Lists.newArrayList();
                        }
                        wordCount++;
                    }
                    //如果等值匹配上,则返回规则
                    //如果是全参数匹配,需要继续向下检索,是否存在等值匹配
                    //保存临时结果集

                    if(paramCount == slashSegmentsLength - wordCount) {
                        return values;
                    }
                    return Lists.newArrayList();
                }
                // /app/add/{name}
                // /app/add/1

                // /app/{name}/add
                // /app/1/add
                if (mixExpressionMatch) {
                    //如果包含前缀
                    if (!isBlankSubApi && url.startsWith(subApi)) {
                        //获取后缀
                        String tempStr = null;
                        if (!isBlankSuffixApi) {
                            tempStr = url.substring(subApiLength + 1);
                        } else {
                            //如果后缀为空,需要继续比较位数
                            if (slashSegmentsLength != sourceSplit.length) {
                                return Lists.newArrayList();
                            }
                        }

                        // add rules:如果属于格式2 同时匹配后缀api
                        if(tempStr == null) {
                            values.add(sourceSplit[sourceSplit.length -1]);
                            return values;
                        }
                        if((tempStr.contains("/") && tempStr.substring(tempStr.indexOf("/")).equals(suffixApi))) {
                            values.add(tempStr.substring(0,tempStr.indexOf("/")));
                            return values;
                        }
                        return Lists.newArrayList();
                    }
                }
                if (!StringUtils.isBlank(temp)) {
                    values.add(temp);
                    return values;
                }
            } catch (Throwable ignore) {
            }
            return Lists.newArrayList();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Matcher)) {
                return false;
            }

            Matcher matcher = (Matcher) o;

            return pattern != null ? pattern.equals(matcher.pattern) : matcher.pattern == null;
        }

        @Override
        public int hashCode() {
            return pattern != null ? pattern.hashCode() : 0;
        }
    }
}
