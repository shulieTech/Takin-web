package com.pamirs.takin.common.redis;

/**
 * 获取 上传 配置key  一定时间内只保存一次
 *
 * @author Administrator
 */
public class UploadInterfaceKeyMaker {

    /**
     * key 前缀
     **/
    private static final String UPLOAD_KEY = "upload_interface_";

    /**
     * 获取key
     */
    public static String getUploadKey(String appName) {
        return UPLOAD_KEY + appName;
    }

}
