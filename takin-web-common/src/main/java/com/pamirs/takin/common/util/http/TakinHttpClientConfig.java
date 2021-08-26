package com.pamirs.takin.common.util.http;

/**
 * @author shulie
 * @description
 * @create 2019-05-25 08:54:33
 */
public class TakinHttpClientConfig {
    private int connectTimeout = 50000;
    private int socketTimeout = 50000;
    private int connectionRequestTimeout = 1000;
    private int maxTotal = 1000;
    private int maxPerRoute = 1000;

    public TakinHttpClientConfig() {
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }
}
