package io.shulie.takin.web.common.client;

import java.util.Date;
import java.util.List;

/**
 * @author shiyajian
 * create: 2020-10-04
 */
public class ClientInfo {

    public static final ClientInfo INSTANCE = new ClientInfo();
    /**
     * 客户端版本
     */
    private String version;
    /**
     * 客户端构建时间
     */
    private String buildTimestamp;
    /**
     * takin-web 控制台的key;
     */
    private String clientKey;
    /**
     * 控制台支持的客户集合
     */
    private List<String> customers;
    /**
     * 控制台的秘钥过期时间
     */
    private Date invalidDate;

    private ClientInfo() { /* no instance */ }

    public static class CustomerInfo {

        /**
         * 客户名称
         */
        private String customerName;

        /**
         * 客户的key
         */
        private String customerKey;
    }
}
