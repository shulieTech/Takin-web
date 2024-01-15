package io.shulie.takin.web.biz.utils;

import java.io.File;
import java.util.jar.JarFile;

public class JarUtils {
    public static Boolean checkClassExist(String jarPath, String className) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
            return jarFile.getEntry(className.replace(".", "/") + ".class") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String jarPath = "/Users/xiaoshu/Documents/apache-jmeter-5.4.1/lib/ext/mysql-connector-java-5.1.25.jar";
        System.out.println(JarUtils.checkClassExist(jarPath, "com.mysql.jdbc.Driver"));
        System.out.println(JarUtils.checkClassExist(jarPath, "com.mysql.jdbc.cj.Driver"));
    }
}
