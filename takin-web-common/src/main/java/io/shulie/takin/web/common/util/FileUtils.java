package io.shulie.takin.web.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/22 6:18 下午
 */
public class FileUtils {
    private static ThreadLocal<CsvReader> csvReaderThreadLocal =
            ThreadLocal.withInitial(() -> CsvUtil.getReader());

    /**
     * 读取Csv文件数据
     *
     * @param path
     * @return
     */
    public static Map<String, List<Object>> readCsv(String path) {
        Map<String, List<Object>> dataMap = Maps.newHashMap();
        CsvData csvData = csvReaderThreadLocal.get().read(FileUtil.file(path));
        // 获取第一行
        List<CsvRow> csvRows = csvData.getRows();
        // 将column和rows做一个映射
        Map<String, String> columnRowMap = Maps.newHashMap();
        boolean isFirstColumn = true;
        for (int i = 0; i < csvRows.size(); i++) {
            int index = 1;
            // 获取第一行
            CsvRow csvRow = csvRows.get(i);
            Iterator it = csvRow.stream().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                // 表示为第一行，第一样是key,设置为column
                if (i == 0) {
                    // TODO 这里有个很难受的事情，第一行,第一列读取的时候，有个点，这里去掉
                    String columnKey = String.valueOf(obj).trim();
                    if (isFirstColumn) {
                        columnKey = columnKey.substring(1);
                        isFirstColumn = false;
                    }
                    dataMap.put(columnKey, Lists.newArrayList());
                    // 每一列对应的column是啥
                    columnRowMap.put(String.valueOf(index), columnKey);
                } else {
                    String columnKey = columnRowMap.get(String.valueOf(index));
                    // 如果超过了第一列的数值,则忽略掉
                    if (StringUtils.isNotBlank(columnKey)) {
                        dataMap.get(columnKey).add(obj);
                    }
                }
                index++;
            }
        }
        return dataMap;
    }

    public static Map<String, String> readCsvFirstRows(String path) {
        Map<String, String> dataMap = Maps.newHashMap();
        CsvData csvData = csvReaderThreadLocal.get().read(FileUtil.file(path));
        // 获取第一行
        CsvRow csvRow = csvData.getRow(0);
        boolean isFirstColumn = true;
        Iterator it = csvRow.stream().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            // 表示为第一行，第一样是key,设置为column
            // TODO 这里有个很难受的事情，第一行,第一列读取的时候，有个点，这里去掉
            String columnKey = String.valueOf(obj).trim();
            if (isFirstColumn) {
                columnKey = columnKey.substring(1);
                isFirstColumn = false;
            }
            dataMap.put(columnKey, "");
        }
        return dataMap;
    }
}
