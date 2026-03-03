package io.shulie.takin.web.app.leqi.main;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultMain {

    /**
     * 按行读取日志文件，获取接口及耗时明细
     * 8f0114ac17574832578380391d027hy01|1757483257838|pressure-engine|0|0|pressure-engine|2468|http|/workbench/cas/login|POST|00|responseToken=jgt1X9Mij1ODXgdY4LYpsNT0FerALizrspXmPY3%2B62Fa6CaJ3Ga%2BdxmHNoQ%2FR5sANE4yE0X%2FJ3egrw..||false~false~false~false~true||#1|@pressure-engine~~~103030|@pressure-engine~~~0~0
     */
    public static Map<String, List<Integer>> statInterfaceCost(String filePath) throws IOException {
        Map<String, List<Integer>> result = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if(parts.length < 9) {
                    System.out.println(parts);
                } else {
                    Integer cost = Integer.parseInt(parts[6]);
                    String url = parts[8];
                    if (result.containsKey(url)) {
                        result.get(url).add(cost);
                    } else {
                        List<Integer> list = new ArrayList<>();
                        list.add(cost);
                        result.put(url, list);
                    }
                }
            }
        }
        return result;
    }
    public static void main(String[] args) throws IOException {
        Map<String, List<Integer>> result = statInterfaceCost("/Users/xiaoshu/Downloads/pressure-1.jtl");
        result.forEach((k, v) -> System.out.println(k+": count="+v.size()+"; avg="+v.stream().mapToInt(Integer::intValue).average().getAsDouble()+"; min="+v.stream().mapToInt(Integer::intValue).min().getAsInt()+"; max="+v.stream().mapToInt(Integer::intValue).max().getAsInt()));
    }
}
