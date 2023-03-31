package io.shulie.takin.web.biz.service.pts;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.pojo.response.pts.JmeterFunctionResponse;

import java.util.List;

public class PtsMain {

    public static void main(String[] args) {
        String str = "[\n" +
                "    {\n" +
                "        \"functionName\":\"changeCase\",\n" +
                "        \"functionDesc\":\"字符串大小写转换\",\n" +
                "        \"functionExample\":\"${__changeCase(abc,UPPER,)}\",\n" +
                "        \"functionParams\":[\n" +
                "            {\n" +
                "                \"param\":\"String\",\n" +
                "                \"describe\":\"字符串\",\n" +
                "                \"required\":true\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"case mode\",\n" +
                "                \"describe\":\"转换模式：默认UPPER-大写，LOWER-小写，CAPITALIZE-首字母大写\",\n" +
                "                \"required\":false\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Variable Name\",\n" +
                "                \"describe\":\"变量名\",\n" +
                "                \"required\":false\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"functionName\":\"dateTimeConvert\",\n" +
                "        \"functionDesc\":\"日期格式转换\",\n" +
                "        \"functionExample\":\"${__dateTimeConvert(2023-01-01 00:00:00,yyyy-MM-dd HH:mm:ss,yyyy-MM-dd,)}\",\n" +
                "        \"functionParams\":[\n" +
                "            {\n" +
                "                \"param\":\"DateTime String\",\n" +
                "                \"describe\":\"时间\",\n" +
                "                \"required\":true\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Source Format\",\n" +
                "                \"describe\":\"源时间格式，如果为空，则时间必须为时间戳\",\n" +
                "                \"required\":false\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Target Format\",\n" +
                "                \"describe\":\"目标时间格式\",\n" +
                "                \"required\":true\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Variable Name\",\n" +
                "                \"describe\":\"变量名\",\n" +
                "                \"required\":false\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"functionName\":\"Random\",\n" +
                "        \"functionDesc\":\"生成一个随机数\",\n" +
                "        \"functionExample\":\"${__Random(1,100,)}\",\n" +
                "        \"functionParams\":[\n" +
                "            {\n" +
                "                \"param\":\"Minimum value\",\n" +
                "                \"describe\":\"最小值\",\n" +
                "                \"required\":true\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Maximum value\",\n" +
                "                \"describe\":\"最大值\",\n" +
                "                \"required\":true\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Variable Name\",\n" +
                "                \"describe\":\"变量名\",\n" +
                "                \"required\":false\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"functionName\":\"RandomDate\",\n" +
                "        \"functionDesc\":\"生成一个随机日期\",\n" +
                "        \"functionExample\":\"${__RandomDate(,,2050-01-01,,)}\",\n" +
                "        \"functionParams\":[\n" +
                "            {\n" +
                "                \"param\":\"Date format\",\n" +
                "                \"describe\":\"日期格式:默认yyyy-MM-dd\",\n" +
                "                \"required\":false\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Start date\",\n" +
                "                \"describe\":\"开始日期，默认当日\",\n" +
                "                \"required\":false\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"End date\",\n" +
                "                \"describe\":\"截止日期,大于当日\",\n" +
                "                \"required\":true\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Variable Name\",\n" +
                "                \"describe\":\"变量名\",\n" +
                "                \"required\":false\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"functionName\":\"RandomString\",\n" +
                "        \"functionDesc\":\"生成一个随机字符串\",\n" +
                "        \"functionExample\":\"${__RandomString(3,abcdef,)}\",\n" +
                "        \"functionParams\":[\n" +
                "            {\n" +
                "                \"param\":\"Length\",\n" +
                "                \"describe\":\"长度\",\n" +
                "                \"required\":true\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Characters to use\",\n" +
                "                \"describe\":\"字符串\",\n" +
                "                \"required\":false\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Variable Name\",\n" +
                "                \"describe\":\"变量名\",\n" +
                "                \"required\":false\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"functionName\":\"time\",\n" +
                "        \"functionDesc\":\"\",\n" +
                "        \"functionExample\":\"${__time(yyyy-MM-dd HH:mm:ss,)}\",\n" +
                "        \"functionParams\":[\n" +
                "            {\n" +
                "                \"param\":\"DateTime format\",\n" +
                "                \"describe\":\"日期格式,默认时间戳\",\n" +
                "                \"required\":false\n" +
                "            },\n" +
                "            {\n" +
                "                \"param\":\"Variable Name\",\n" +
                "                \"describe\":\"变量名\",\n" +
                "                \"required\":false\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"functionName\":\"urlencode\",\n" +
                "        \"functionDesc\":\"将字符串进行URL编码\",\n" +
                "        \"functionExample\":\"${__urlencode(aaa)}\",\n" +
                "        \"functionParams\":[\n" +
                "            {\n" +
                "                \"param\":\"String to encode\",\n" +
                "                \"describe\":\"需要编码的字符串\",\n" +
                "                \"required\":true\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"functionName\":\"UUID\",\n" +
                "        \"functionDesc\":\"生成唯一识别码\",\n" +
                "        \"functionExample\":\"${__UUID}\",\n" +
                "        \"functionParams\":[\n" +
                "\n" +
                "        ]\n" +
                "    }\n" +
                "]";

        List<JmeterFunctionResponse> responseList = JSON.parseArray(str, JmeterFunctionResponse.class);
        for(int i = 0; i < responseList.size(); i++) {
            System.out.println(JSON.toJSONString(responseList.get(i)));
        }
    }
}
