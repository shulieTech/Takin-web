package io.shulie.takin.cloud.common.utils;

import java.util.Date;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

/**
 * 压测时长工具类
 *
 * @author 张天赐
 */
public class TestTimeUtil {

    /**
     * 压测时长
     * <p>
     * 格式化说明:<br/>
     * 无启动时间/无停止时间，返回null<br/>
     * 停止时间-启动时间
     * </p>
     *
     * @param startTime 启动时间
     * @param endTime   停止时间
     * @return -h-'-"(-小时-分钟-秒钟)
     */
    public static String format(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {return null;}
        // 基础差额
        long ms = DateUtil.between(startTime, endTime, DateUnit.MS);
        // 换算成小时
        long hour = ms / DateUnit.HOUR.getMillis();
        // 减去基数
        ms = ms - (hour * DateUnit.HOUR.getMillis());
        // 换算成分钟
        long minute = ms / DateUnit.MINUTE.getMillis();
        // 减去基数
        ms = ms - (minute * DateUnit.MINUTE.getMillis());
        // 换算成分钟
        long second = ms / DateUnit.SECOND.getMillis();
        // 组装返回
        return String.format("%dh %d'%d\"", hour, minute, second);
    }
}
