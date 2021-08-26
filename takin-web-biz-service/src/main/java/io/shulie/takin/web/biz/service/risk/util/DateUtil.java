package io.shulie.takin.web.biz.service.risk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xingchen
 * @ClassName: DateUtil
 * @Package: com.pamirs.takin.web.api.service.risk.util
 * @date 2020/7/2819:32
 */
public class DateUtil {
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static ThreadLocal<SimpleDateFormat> secondSimpledateFormatter = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setLenient(false);
        return format;
    });

    public static Date parseSecondFormatter(String date) {
        try {
            return secondSimpledateFormatter.get().parse(date);
        } catch (ParseException e) {
            logger.error("时间格式错误{}", date);
        }
        return new Date();
    }
}
