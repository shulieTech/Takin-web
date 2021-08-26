/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.web.biz.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lipeng
 * @date 2021-05-17 1:41 下午
 */
public class JmeterTraceIdGeneratorUtil {

    private static String IP_16 = "ffffffff";
    private static String IP_int = "255255255255";
    private static String PID = "0000";
    private static final char PID_FLAG = 'd';

    private static final String REGEX
            = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.("
            + "(?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final AtomicInteger COUNT = new AtomicInteger(1000);

    static {
        try {
            String ipAddress = getLocalAddress();
            if (ipAddress != null) {
                IP_16 = getIP_16(ipAddress);
                IP_int = getIP_int(ipAddress);
            }

            PID = getHexPid(getPid());
        } catch (Throwable e) {
        }
    }

    private static String getLocalAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Throwable t) {
        }
        return "127.0.0.1";
    }

    static String getHexPid(int pid) {
        // unsign short 0 to 65535
        if (pid < 0) {
            pid = 0;
        }
        if (pid > 65535) {
            String strPid = Integer.toString(pid);
            strPid = strPid.substring(strPid.length() - 4, strPid.length());
            pid = Integer.parseInt(strPid);
        }
        StringBuilder str = new StringBuilder(Integer.toHexString(pid));
        while (str.length() < 4) {
            str.insert(0, "0");
        }
        return str.toString();
    }

    /**
     * get current pid,max pid 32 bit systems 32768, for 64 bit 4194304
     * http://unix.stackexchange.com/questions/16883/what-is-the-maximum-value-of-the-pid-of-a-process
     * <p>
     * http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
     *
     * @return
     */
    static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        int pid;
        try {
            pid = Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            pid = 0;
        }
        return pid;
    }

    private static String getTraceId(String ip, long timestamp, int nextId) {
        StringBuilder appender = new StringBuilder(32);
        appender.append(ip).append(timestamp).append(nextId).append(PID_FLAG).append(PID);
        return appender.toString();
    }

    public static String generate() {
        return getTraceId(IP_16, System.currentTimeMillis(), getNextId());
    }

    public static String generateAllSampled() {
        return getTraceId(IP_16, System.currentTimeMillis(), getNextAllSampleId());
    }

    public static String generate(String ip) {
        if (StringUtils.isNotBlank(ip) && validate(ip)) {
            return getTraceId(getIP_16(ip), System.currentTimeMillis(), getNextId());
        } else {
            return generate();
        }
    }

    public static String generateIpv4Id() {
        return IP_int;
    }

    public static String generateIpv4Id(String ip) {
        if (ip != null && !ip.isEmpty() && validate(ip)) {
            return getIP_int(ip);
        } else {
            return IP_int;
        }
    }

    private static boolean validate(String ip) {
        try {
            return PATTERN.matcher(ip).matches();
        } catch (Throwable e) {
            return false;
        }
    }

    private static String getIP_16(String ip) {
        String[] ips = StringUtils.split(ip, '.');
        StringBuilder sb = new StringBuilder();
        for (int i = ips.length - 1; i >= 0; --i) {
            String hex = Integer.toHexString(Integer.parseInt(ips[i]));
            if (hex.length() == 1) {
                sb.append('0').append(hex);
            } else {
                sb.append(hex);
            }

        }
        return sb.toString();
    }

    private static String getIP_int(String ip) {
        return ip.replace(".", "");
    }

    private static int getNextId() {
        for (; ; ) {
            int current = COUNT.get();
            int next = (current > 9000) ? 1000 : current + 1;
            if (COUNT.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    /**
     * 全采样nextid
     *
     * @return
     */
    private static int getNextAllSampleId() {
        for (; ; ) {
            int current = COUNT.get();
            int next = (current > 8000) ? 1000 : current + 1000;
            if (COUNT.compareAndSet(current, next)) {
                return next;
            }
        }
    }

}
