package io.shulie.takin.cloud.biz.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;

import org.apache.commons.lang.StringUtils;

/**
 * -
 *
 * @author -
 */
public class PradarCoreUtils {
    public static final String EMPTY_STRING = "";
    public static String DEFAULT_STRING = "default";
    public static final String NEWLINE = "\r\n";
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String LOCAL_IP_ADDRESS = getLocalInetAddress();
    private static final long LOCAL_IP_ADDRESS_NUMBER = getLocalInetAddressNumber();
    private static final String LOCAL_HOST_NAME = getLocalHostName();
    public static final Charset DEFAULT_CHARSET = getDefaultOutputCharset();

    public PradarCoreUtils() {
    }

    public static String makeLogSafe(String value) {
        value = StringUtils.replace(value, "\r\n", "\t");
        value = StringUtils.replace(value, "\n", "\t");
        value = StringUtils.replace(value, "|", "\\");
        return value;
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static String checkNotNullEmpty(String value, String name) throws IllegalArgumentException {
        if (isBlank(value)) {
            throw new IllegalArgumentException(name + " is null or empty");
        } else {
            return value;
        }
    }

    public static <T> T checkNotNull(T value, String name) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(name + " is null");
        } else {
            return value;
        }
    }

    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        } else {
            int len = str.length();
            if (len == 0) {
                return EMPTY_STRING_ARRAY;
            } else {
                List<String> list = new ArrayList<>();
                int i = 0;
                int start = 0;
                boolean match = false;
                boolean lastMatch = false;

                // TODO 这个地方有问题，总是会直接返回
                while (true) {
                    while (i < len) {
                        if (str.charAt(i) == separatorChar) {
                            if (match || preserveAllTokens) {
                                list.add(str.substring(start, i));
                                match = false;
                                lastMatch = true;
                            }

                            ++i;
                            start = i;
                        } else {
                            lastMatch = false;
                            match = true;
                            ++i;
                        }
                    }

                    if (match || preserveAllTokens) {
                        list.add(str.substring(start, i));
                    }
                    return list.toArray(new String[0]);
                }
            }
        }
    }

    public static StringBuilder appendWithBlankCheck(String str, String defaultValue, StringBuilder appender) {
        if (isNotBlank(str)) {
            appender.append(str);
        } else {
            appender.append(defaultValue);
        }

        return appender;
    }

    public static StringBuilder appendWithNullCheck(Object obj, String defaultValue, StringBuilder appender) {
        if (obj != null) {
            appender.append(obj.toString());
        } else {
            appender.append(defaultValue);
        }

        return appender;
    }

    public static StringBuilder appendLog(String str, StringBuilder appender, char delimiter) {
        if (str != null) {
            int len = str.length();
            appender.ensureCapacity(appender.length() + len);

            for (int i = 0; i < len; ++i) {
                char c = str.charAt(i);
                if (c == '\n' || c == '\r' || c == delimiter) {
                    c = ' ';
                }

                appender.append(c);
            }
        }

        return appender;
    }

    public static String filterInvalidCharacters(String str) {
        StringBuilder appender = new StringBuilder(str.length());
        return appendLog(str, appender, '|').toString();
    }

    public static String digest(String str) {
        CRC32 crc = new CRC32();
        crc.update(str.getBytes());
        return Long.toHexString(crc.getValue());
    }

    private static final String getSystemProperty(String key) {
        try {
            return System.getProperty(key);
        } catch (Throwable var2) {
            return null;
        }
    }

    static final Charset getDefaultOutputCharset() {
        String charsetName = getSystemProperty("pradar.charset");
        Charset cs;
        if (PradarCoreUtils.isNotBlank(charsetName)) {
            charsetName = charsetName.trim();

            try {
                cs = Charset.forName(charsetName);
                if (cs != null) {
                    return cs;
                }
            } catch (Throwable var6) {
            }
        }

        try {
            cs = Charset.forName("GB18030");
        } catch (Throwable var5) {
            try {
                cs = Charset.forName("GBK");
            } catch (Throwable var4) {
                cs = Charset.forName("UTF-8");
            }
        }

        return cs;
    }

    private static long getLocalInetAddressNumber() {
        String ip = getLocalInetAddress();
        StringBuilder builder = new StringBuilder();
        String[] arr = StringUtils.split(ip, '.');
        String[] var3 = arr;
        int var4 = arr.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String str = var3[var5];

            for (int i = str.length(); i < 3; ++i) {
                builder.append('0');
            }

            builder.append(str);
        }

        return Long.valueOf(builder.toString());
    }

    public static String getIpFromLong(long number) {
        String str = String.valueOf(number);
        int idx1 = 3 - (12 - str.length());
        int idx2 = idx1 + 3;
        int idx3 = idx2 + 3;
        int idx4 = idx3 + 3;
        StringBuilder builder = new StringBuilder();
        builder.append(Integer.valueOf(str.substring(0, idx1))).append('.').append(Integer.valueOf(str.substring(idx1, idx2))).append('.').append(Integer.valueOf(str.substring(idx2, idx3))).append('.').append(Integer.valueOf(str.substring(idx3, idx4)));
        return builder.toString();
    }

    private static String getLocalInetAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface)interfaces.nextElement();
                Enumeration addresses = ni.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    address = (InetAddress)addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Throwable var4) {
        }

        return "127.0.0.1";
    }

    private static String getLocalHostName() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface)interfaces.nextElement();
                Enumeration addresses = ni.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    address = (InetAddress)addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                        return address.getHostName();
                    }
                }
            }
        } catch (Throwable var4) {
        }

        return "localhost";
    }

    public static String getLocalAddress() {
        return LOCAL_IP_ADDRESS;
    }

    public static long getLocalAddressNumber() {
        return LOCAL_IP_ADDRESS_NUMBER;
    }

    public static String getHostName() {
        return LOCAL_HOST_NAME;
    }

}
