package io.shulie.takin.web.api.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.mortbay.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author fanxx
 * @date 2020/10/19 11:18 上午
 */
public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String sendGet(String inputUrl, Object object, Map<String, String> headerParams) {
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
            conn = (HttpURLConnection)url.openConnection();
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
                    BufferedReader responseReader = new BufferedReader(
                        new InputStreamReader(in1, StandardCharsets.UTF_8));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    log.debug(sb.toString());
                } catch (Exception e1) {
                    throw new RuntimeException("读取返回数据时发生错误！",e1);
                }
            } else {
                log.error("请求路径为：{},状态码为：{},错误信息为:{}", url, conn.getResponseCode(), conn.getResponseMessage());
                throw new RuntimeException("服务端发生异常！");
            }
        } catch (ConnectException e) {
            throw new RuntimeException("服务连接失败，请检查服务是否可用！", e);
        } catch (SocketTimeoutException e) {
            throw new RuntimeException("网络超时，无法连接远程服务！",e);
        }  catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
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
                if (entry.getValue() == null) {
                    continue;
                }
                buffer.append(entry.getKey());
                buffer.append("=");
                buffer.append(UrlEncoded.encodeString(entry.getValue().toString(), "UTF-8"));
                buffer.append("&");
            }
        }
        return buffer.toString();
    }

    public static String sendPost(String inputUrl, Object object, Map<String, String> headerParams) {
        if (!(object instanceof JSON)) {
            object = JSON.toJSON(object);
        }
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            // 创建url资源
            URL url = new URL(inputUrl);
            // 建立http连接
            conn = (HttpURLConnection)url.openConnection();
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
            // 头部参数
            if (headerParams != null && headerParams.size() > 0) {
                for (Map.Entry<String, String> node : headerParams.entrySet()) {
                    conn.setRequestProperty(node.getKey(), node.getValue());
                }
            }
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
                    BufferedReader responseReader = new BufferedReader(
                        new InputStreamReader(in1, StandardCharsets.UTF_8));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                } catch (Exception e1) {
                    throw new RuntimeException("读取返回数据时发生错误！",e1);
                }
            } else {
                log.error("请求路径为：{},状态码为：{},错误信息为:{}", url, conn.getResponseCode(), conn.getResponseMessage());
                throw new RuntimeException("服务端发生异常！");
            }
        } catch (ConnectException e) {
            throw new RuntimeException("服务连接失败，请检查服务是否可用！", e);
        } catch (SocketTimeoutException e) {
            throw new RuntimeException("网络超时，无法连接远程服务！",e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }
}
