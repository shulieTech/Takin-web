package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * @author 无涯
 * @Package io.shulie.tro.web.common.vo.fastdebug
 * @date 2020/12/28 11:11 上午
 */
@Data
public class ContentTypeVO {
    public static Integer X_WWW_FORM_URLENCODED = 0;
    public static Integer RAW = 1;
    public static String UTF_8 = "utf-8";
    public static String GBK = "gbk";
    public static Map<String,String> map = Maps.newHashMap();
    /**
     *  x-www-form-urlencoded:0, raw:1;
     */
    private Integer radio;
    /**
     * 请求类型
     */
    private String type;
    /**
     * 编码格式
     */
    private String codingFormat;
}
