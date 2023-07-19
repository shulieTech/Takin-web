package io.shulie.takin.cloud.common.bean;

import lombok.Data;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

@Data
public class HttpUrlBean implements Serializable {

    private String protocol;

    private String host;

    private Integer port;

    private String path;

    private String query;


    public static HttpUrlBean convertRequestUrl2Bean(String requestUrl) {
        HttpUrlBean urlBean = new HttpUrlBean();
        try {
            URL url = new URL(requestUrl);
            urlBean.setProtocol(url.getProtocol());
            urlBean.setHost(url.getHost());
            urlBean.setPort(url.getPort());
            urlBean.setPath(url.getPath());
            urlBean.setQuery(url.getQuery());
        } catch (MalformedURLException e) {
            urlBean.setPath(requestUrl);
        }
        return urlBean;
    }
}
