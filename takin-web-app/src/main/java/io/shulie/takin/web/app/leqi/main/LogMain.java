package io.shulie.takin.web.app.leqi.main;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogMain {
    /**
     * 按行读取日志文件，如果包含关键字abc，则输出该行（截取长度512）
     */
    public static List<String> filterLogsByKeyword(String filePath, String[] keywords) throws IOException {
        List<String> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.containsAny(line, keywords)) {
                    result.add(line.length() > 512 ? line.substring(0, 512) : line);
                }
            }
        }
        return result;
    }
    public static void main(String[] args) throws IOException {
        String[] keywords = {"25090900000855127452", "25090900200014337534"};
        List<String> result = filterLogsByKeyword("/Users/xiaoshu/Downloads/application.log", keywords);
        for (String line : result) {
            System.out.println(line);
        }
    }
}

