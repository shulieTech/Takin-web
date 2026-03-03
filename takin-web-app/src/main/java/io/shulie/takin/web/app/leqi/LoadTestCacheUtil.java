package io.shulie.takin.web.app.leqi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadTestCacheUtil {
    private static Map<String, List<String>> dataMap = new HashMap<>();
    public static List<String> getInvoiceNo(String key) {
        return dataMap.get(key);
    }

    public static void putInvoiceNo(String key, List<String> value) {
        dataMap.put(key, value);
    }
}
