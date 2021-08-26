package com.pamirs.takin.common.util.http;

import java.util.Date;
import java.time.ZoneId;
import java.time.Instant;
import java.time.Duration;
import java.time.LocalDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

/**
 * 基于JDK 1.8 日期API
 *
 * @author xingchen
 * @date 2019/03/10下午2:21
 */
public class DateUtil {
    private static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static String formatTime(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS);
        return dateFormat.format(timestamp);
    }

    /**
     * 将日期转为字符串 yyyy-mm-dd HH:mm:ss
     *
     * @param date -
     * @return -
     */
    public static String getYYYYMMDDHHMMSS(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);
        LocalDateTime localDateTime = dateToDateTime(date);
        return formatter.format(localDateTime);
    }

    /**
     * 将日期转为字符串 yyyy-mm-dd HH:mm:ss
     *
     * @param date -
     * @return -
     */
    public static String getDate(Date date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = dateToDateTime(date);
        return formatter.format(localDateTime);
    }

    /**
     * 将字符串转为日期
     *
     * @param date -
     * @return -
     */
    public static Date getDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        return dateTimeToDate(localDateTime);
    }

    /**
     * 将字符串转为日期
     *
     * @param date -
     * @return -
     */
    public static Date getDate(String date, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        return dateTimeToDate(localDateTime);
    }

    /**
     * LocalDateTime 和DateTime 互转
     *
     * @param date -
     * @return -
     */
    public static LocalDateTime dateToDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Date dateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 两个日期相差多少秒
     *
     * @param date1 -
     * @param date2 -
     * @return -
     */
    public static Long getUntilSecond(Date date1, Date date2) {
        LocalDateTime localDate1 = dateToDateTime(date1);
        LocalDateTime localDate2 = dateToDateTime(date2);
        return Math.abs(Duration.between(localDate1, localDate2).get(ChronoUnit.SECONDS));
    }

    /**
     * 获取当前时间后几分钟的时间
     *
     * @param date   -
     * @param minute -
     * @return -
     */
    public static Date afterXMinuteDate(Date date, long minute) {
        LocalDateTime localDate = dateToDateTime(date);
        LocalDateTime afterTime = localDate.plusMinutes(minute);
        return dateTimeToDate(afterTime);
    }

    /**
     * 获取当前时间后几分钟的时间
     *
     * @param date   -
     * @param minute -
     * @return -
     */
    public static Date preXMinuteDate(Date date, long minute) {
        LocalDateTime localDate = dateToDateTime(date);
        LocalDateTime afterTime = localDate.minusMinutes(minute);
        return dateTimeToDate(afterTime);
    }

    /**
     * 将时间转换为时间戳
     */
    public static Long dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime();
    }
}
