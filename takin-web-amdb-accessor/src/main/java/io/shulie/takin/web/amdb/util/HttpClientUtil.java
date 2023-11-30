package io.shulie.takin.web.amdb.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.mortbay.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fanxx
 * @date 2020/10/19 11:18 上午
 */
public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String sendGet(String inputUrl, Object object) {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            if (!inputUrl.contains("?")) {
                inputUrl += "?";
            }
            inputUrl += parseParams(object);
            // 创建url资源
            URL url = new URL(inputUrl);
            // 建立http连接
            conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(false);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("GET");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;");
            // 设置超时时间 设置10s —>由于大数据查询较慢 设置为30s
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            // 开始连接请求
            conn.connect();
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, StandardCharsets.UTF_8));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    log.debug(sb.toString());
                } catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            } else {
                log.error("http请求失败，请求路径为：{},状态码为：{},错误信息为:{}", conn.getURL(), conn.getResponseCode(), conn.getResponseMessage());
                throw new RuntimeException("http请求失败，请求路径为：" + conn.getURL() + ",状态码为：" + conn.getResponseCode() + ",错误信息为：" + conn.getResponseMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }
    public static String parseParams(Object object) {
        String s = JSON.toJSONString(object);
        JSONObject jsonObject = JSON.parseObject(s);
        StringBuilder buffer = new StringBuilder();
        if (jsonObject != null && !jsonObject.isEmpty()) {
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                if (entry.getValue() == null || entry.getKey().isEmpty()) {
                    continue;
                }
                try {
                    buffer.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    buffer.append("=");
                    buffer.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    buffer.append("&");
                } catch (UnsupportedEncodingException e) {
                    // 这个异常实际上不会发生，因为 UTF-8 总是被支持的
                    e.printStackTrace();
                }
            }
            if (buffer.length() > 0) {
                buffer.deleteCharAt(buffer.length() - 1); // 删除最后一个 "&"
            }
        }
        return buffer.toString();
    }

    public static String sendGet(String inputUrl) {
        return sendGet(inputUrl, null);
    }

    public static String sendGet(String inputUrl, Map<String, String> headerParams) {
        log.debug("HttpUtil sendGet URL:" + inputUrl);
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            // 创建url资源
            URL url = new URL(inputUrl);
            // 建立http连接
            conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(false);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("GET");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;");
            // 头部参数
            if (headerParams != null && headerParams.size() > 0) {
                for (Map.Entry<String, String> node : headerParams.entrySet()) {
                    conn.setRequestProperty(node.getKey(), node.getValue());
                }
            }
            // 开始连接请求
            conn.connect();
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, StandardCharsets.UTF_8));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    log.debug(sb.toString());
                } catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            } else {
                log.error("http请求失败，请求路径为：{},状态码为：{},错误信息为:{}", url, conn.getResponseCode(), conn.getResponseMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }

    public static String sendPost(String inputUrl, Object object) {
        if (!(object instanceof JSON)) {
            object = JSON.toJSON(object);
        }
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            // 创建url资源
            URL url = new URL(inputUrl);
            // 建立http连接
            conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = (object.toString()).getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            // 开始连接请求
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.write((object.toString()).getBytes());
            out.flush();
            out.close();
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, StandardCharsets.UTF_8));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    log.debug("HttpUtil sendGet URL:" + sb);
                } catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            } else {
                log.error("error++,url:{},responseCode:{},responseMessage:{}", inputUrl, conn.getResponseCode(), conn.getResponseMessage());
                throw new RuntimeException(String.format("http请求失败，请求路径为：%s,状态码为：%s,错误信息为：%s,请求method为：%s", inputUrl, conn.getResponseCode(), conn.getResponseMessage(),conn.getRequestMethod()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }
}
