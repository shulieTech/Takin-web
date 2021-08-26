package io.shulie.takin.web.biz.utils;

import io.shulie.takin.web.common.constant.WhiteListConstants;

/**
 * @author liuchuan
 * @date 2021/4/13 5:29 下午
 */
public class WhiteListUtil {

    /**
     * 获取 interface, type 组装的唯一
     *
     * @param interfaceName 接口名
     * @param interfaceType 类型
     * @return 唯一
     */
    public static String getInterfaceAndType(String interfaceName, String interfaceType) {
        return String.format(WhiteListConstants.INTERFACE_AND_TYPE, interfaceName, interfaceType);
    }

}
