package com.pamirs.takin.common.util;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import io.shulie.takin.utils.string.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: vernon
 * @Date: 2022/3/17 19:22
 * @Description: http工具类
 */
public abstract class HttpSupport {


    public enum HttpMethodType {
        get, put, post, delete, options, trace, head, patch
    }


    private static List<HttpSupport> httpSupportList = Arrays.stream(HttpMethodType.values())
            .map(value -> new HttpSupport() {
                @Override
                public String methodName() {
                    return value.name().toUpperCase();
                }
            }).collect(Collectors.toList());


    private static Object lock = new Object();

    private static LoadingCache<String, HttpSupport> httpSupportLoadingCache;


    public static LoadingCache<String, HttpSupport> get() {
        if (httpSupportLoadingCache == null) {
            synchronized (lock) {
                if (null == httpSupportLoadingCache) {
                    httpSupportLoadingCache = build();
                }
            }
        }
        return httpSupportLoadingCache;
    }


    private static LoadingCache<String, HttpSupport> build() {
        return CacheBuilder.newBuilder()
                .build(
                        new CacheLoader<String, HttpSupport>() {

                            @Override
                            public HttpSupport load(String key) throws Exception {
                                return httpSupportList.stream().filter(http ->
                                        http.methodName().equals(key.toLowerCase())
                                                || http.methodName().equals(key.toUpperCase()))
                                        .collect(Collectors.toList()).get(0);
                            }
                        }
                );
    }


    public static final Logger logger = LoggerFactory.getLogger(HttpSupport.class);

    protected static final Charset UTF_8 = Charset.forName("UTF-8");

    public abstract String methodName();

    /**
     * 支持直接输出流
     *
     * @param url
     * @param body
     * @param header
     * @param response
     * @return
     */
    public ResponseWrapper to(String url, Object body, Map<String, Object> header, HttpServletResponse response) {
        HostPort hostPort = getHostPortUrlFromUrl(url);
        return to(hostPort.host, hostPort.port, hostPort.url, body, header, response);
    }

    /**
     * 不支持直接输出流
     *
     * @param url
     * @param body
     * @param header
     * @return
     */
    public ResponseWrapper to(String url, Object body, Map<String, Object> header) {
        HostPort hostPort = getHostPortUrlFromUrl(url);
        return to(hostPort.host, hostPort.port, hostPort.url, body, header, null);
    }

    private ResponseWrapper to(String host, int port, String url, Object body, Map<String, Object> header
            , HttpServletResponse httpServletResponse) {

        InputStream input = null;
        OutputStream output = null;
        Socket socket = null;
        try {
            SocketAddress address = new InetSocketAddress(host, port);
            socket = new Socket();
            socket.connect(address, 1000); // 设置建立连接超时时间 1s
            //socket.setSoTimeout(5000); // 设置读取数据超时时间 5s
            socket.setSoTimeout(10000);
            output = socket.getOutputStream();

            StringBuilder request = new StringBuilder(methodName().toUpperCase()).append(" ").append(url).append(" HTTP/1.1\r\n");
            if (header == null || !header.containsKey("Host")) {
                request.append("Host: ").append(host).append(":").append(port).append("\r\n");
            }
            if (header == null || !header.containsKey("Connection")) {
                request.append("Connection: Keep-Alive\r\n");
            }


            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    request.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
                }
            }
            if (body != null && body instanceof String) {
                //字符串
                String bodyStr = (String) body;
                if (!bodyStr.isEmpty()) {
                    if (header == null || !header.containsKey("Content-Length")) {
                        request.append("Content-Length: ").append(bodyStr.getBytes().length).append("\r\n");
                    }
                    if (header == null || !header.containsKey("Content-Type")) {
                        request.append("Content-Type: application/json\r\n");
                    }


                }

                request.append("\r\n");
                output.write(request.toString().getBytes(UTF_8));

                if (!bodyStr.isEmpty()) {
                    output.write(bodyStr.getBytes(UTF_8));
                }
            } else if (body != null && body instanceof byte[]) {
                //字节
                byte[] bodyBytes = (byte[]) body;
                if (bodyBytes.length != 0) {
                    if (header == null || !header.containsKey("Content-Length")) {
                        request.append("Content-Length: ").append(bodyBytes.length).append("\r\n");
                    }
                    if (header == null || !header.containsKey("Content-Type")) {
                        request.append("Content-Type: application/json\r\n");
                    }

                }

                request.append("\r\n");
                output.write(request.toString().getBytes(UTF_8));

                if (bodyBytes.length != 0) {
                    output.write(bodyBytes);
                }
            } else {
                request.append("\r\n");
                output.write(request.toString().getBytes(UTF_8));
            }


            output.flush();

            input = socket.getInputStream();

            String status = readLine(input);

            Map<String, List<String>> responseHeaders = readHeaders(input);
            /**
             * 特殊的流直接响应了
             */
            if (specialBuffer(responseHeaders) && Objects.nonNull(httpServletResponse)) {
                doResponse(httpServletResponse, input, responseHeaders);
                return ResponseWrapper.IGNORE;

            }
            input = wrapperInput(responseHeaders, input);
            return wrapperResult(responseHeaders, toString(input), status);
        } catch (IOException e) {
            String to = host.concat(":").concat(String.valueOf(port)).concat(url);
            logger.error("[{}] http request error : {} ", to, e.getMessage());
            return ResponseWrapper.Error().setData("[" + to + "] : " + e.getMessage());
        } finally {
            closeQuietly(input);
            closeQuietly(output);

            // JDK 1.6 Socket没有实现Closeable接口
            if (socket != null) {
                try {
                    socket.close();
                } catch (final IOException ioe) {
                    // ignore
                }
            }
        }
    }

    public boolean specialBuffer(Map<String, List<String>> headers) {
        return (CollectionUtils.isNotEmpty(headers.get("Content-Type"))
                && headers.get("Content-Type").contains("application/zip"))
                || (CollectionUtils.isNotEmpty(headers.get("Content-Encoding")) && headers.get("Content-Type").contains("gzip"));
    }

    public static void doResponse(HttpServletResponse resp, InputStream inputStream, Map<String, List<String>> header) {

        if (header != null && !header.isEmpty()) {
            Iterator<Map.Entry<String, List<String>>> iterator = header.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> entry = iterator.next();
                resp.setHeader(entry.getKey(), Joiner.on(",").join(entry.getValue()));
            }
        }
        try {
            OutputStream os = resp.getOutputStream();
            byte[] buffer = new byte[4096];
            int i = inputStream.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = inputStream.read(buffer);
            }
            os.close();
            inputStream.close();

        } catch (Exception r) {
            logger.error(r.toString());
        }
    }


    public static ResponseWrapper wrapperResult(Map<String, List<String>> headers, String data, String status) {
        if (!StringUtil.isEmpty(status)) {
            status = status.trim().split(" ")[1].trim();
        }
        Map<String, String> returnHeader = Maps.newHashMap();

        if (headers != null && !headers.isEmpty()) {

            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                List<String> valueList = headers.get(key);
                if (CollectionUtils.isNotEmpty(valueList)) {
                    returnHeader.put(key, Joiner.on(",").join(valueList));
                }
            }
        }
        return ResponseWrapper
                .build()
                .setData(data)
                .setHeader(returnHeader)
                .setStatus(status);
    }

    private static class HostPort {
        public String host;
        public int port;
        public String url;

        @Override
        public String toString() {
            return "HostPort{" +
                    "host='" + host + '\'' +
                    ", port=" + port +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    private static final Pattern URL_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?",
            Pattern.CASE_INSENSITIVE);

    private static HostPort getHostPortUrlFromUrl(String url) {
        String domain = url;
        String restUrl = url;
        Matcher matcher = URL_PATTERN.matcher(url);
        if (matcher.find()) {
            String group = matcher.group();
            domain = group.substring(group.indexOf("//") + 2, group.length());
            restUrl = url.substring(url.indexOf(group) + group.length(), url.length());
        }

        HostPort hostPort = new HostPort();
        hostPort.url = restUrl;
        if (!domain.contains(":")) {
            hostPort.host = domain;
            hostPort.port = 80;
        } else {
            hostPort.host = domain.substring(0, domain.indexOf(":"));
            hostPort.port = Integer.parseInt(domain.substring(domain.indexOf(":") + 1, domain.length()));
        }
        return hostPort;
    }


    public String toString(InputStream input) throws IOException {
        ByteArrayOutputStream content = null;
        try {
            content = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = input.read(buffer)) > 0) {
                content.write(buffer, 0, len);
            }
            return new String(content.toByteArray(), UTF_8);
        } finally {
            closeQuietly(content);
        }

    }

    public String readLine(InputStream input) throws IOException {
        ByteArrayOutputStream bufdata = new ByteArrayOutputStream();
        int ch;
        while ((ch = input.read()) >= 0) {
            bufdata.write(ch);
            if (ch == '\n') {
                break;
            }
        }
        if (bufdata.size() == 0) {
            return null;
        }
        byte[] rawdata = bufdata.toByteArray();
        int len = rawdata.length;
        int offset = 0;
        if (len > 0) {
            if (rawdata[len - 1] == '\n') {
                offset++;
                if (len > 1) {
                    if (rawdata[len - 2] == '\r') {
                        offset++;
                    }
                }
            }
        }
        return new String(rawdata, 0, len - offset, UTF_8);
    }

    public InputStream wrapperInput(Map<String, List<String>> headers, InputStream input) {
        List<String> transferEncodings = headers.get("Transfer-Encoding");
        if (transferEncodings != null && !transferEncodings.isEmpty()) {
            String encodings = transferEncodings.get(0);
            String[] elements = encodings.split(";");
            int len = elements.length;
            if (len > 0 && "chunked".equalsIgnoreCase(elements[len - 1])) {
                return new ChunkedInputStream(input);
            }
            return input;
        }
        List<String> contentLengths = headers.get("Content-Length");
        if (contentLengths != null && !contentLengths.isEmpty()) {
            long length = -1;
            for (String contentLength : contentLengths) {
                try {
                    length = Long.parseLong(contentLength);
                    break;
                } catch (final NumberFormatException ignore) {
                    // ignored
                }
            }
            if (length >= 0) {
                return new ContentLengthInputStream(input, length);
            }
        }
        return input;
    }

    public Map<String, List<String>> readHeaders(InputStream input)
            throws IOException {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        String line = readLine(input);
        while (line != null && !line.isEmpty()) {
            String[] headerPair = line.split(":");
            String name = headerPair[0].trim();
            String value = headerPair[1].trim();
            List<String> values = headers.get(name);
            if (values == null) {
                values = new ArrayList<String>();
                headers.put(name, values);
            }
            values.add(value);
            line = readLine(input);
        }
        return headers;
    }

    public void exhaustInputStream(InputStream inStream)
            throws IOException {
        byte buffer[] = new byte[1024];
        while (inStream.read(buffer) >= 0) {
        }
    }

    public void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException ioe) {
                // ignore
            }
        }
    }


}

