package io.shulie.takin.web.amdb.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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

    public static String encodeString(String string, Charset charset) {
        if (charset == null) {
            charset = Charset.defaultCharset();
        }

        byte[] bytes = string.getBytes(charset);
        byte[] encoded = new byte[bytes.length * 3];
        int n = 0;
        boolean noEncode = true;
        byte[] var6 = bytes;
        int var7 = bytes.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            byte b = var6[var8];
            if (b == 32) {
                noEncode = false;
                encoded[n++] = 43;
            } else if ((b < 97 || b > 122) && (b < 65 || b > 90) && (b < 48 || b > 57) && b != 45 && b != 46 && b != 95 && b != 126) {
                noEncode = false;
                encoded[n++] = 37;
                byte nibble = (byte) ((b & 240) >> 4);
                if (nibble >= 10) {
                    encoded[n++] = (byte) (65 + nibble - 10);
                } else {
                    encoded[n++] = (byte) (48 + nibble);
                }

                nibble = (byte) (b & 15);
                if (nibble >= 10) {
                    encoded[n++] = (byte) (65 + nibble - 10);
                } else {
                    encoded[n++] = (byte) (48 + nibble);
                }
            } else {
                encoded[n++] = b;
            }
        }

        if (noEncode) {
            return string;
        } else {
            return new String(encoded, 0, n, charset);
        }
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
                buffer.append(encodeString(entry.getValue().toString(), Charset.forName("UTF-8")));
                buffer.append("&");
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
                log.error("error++,{}", conn.getResponseMessage());
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
