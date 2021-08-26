package io.shulie.takin.web.biz.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 容量工具类
 *
 * @author qianshui
 * @Description byte转GB
 * @date 2020/8/7 下午3:56
 */
public class VolumnUtil {

    /**
     * byte转GB
     *
     * @param byteSize
     * @return
     */
    public static BigDecimal convertByte2Gb(BigDecimal byteSize) {
        return byteSize.divide(new BigDecimal(1024 * 1024 * 1204), 2, RoundingMode.HALF_UP);
    }
}
