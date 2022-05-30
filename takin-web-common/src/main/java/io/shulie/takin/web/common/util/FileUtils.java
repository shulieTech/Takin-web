package io.shulie.takin.web.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/22 6:18 下午
 */
@Slf4j
public class FileUtils {
    private static ThreadLocal<CsvReader> csvReaderThreadLocal = ThreadLocal.withInitial(() -> CsvUtil.getReader());

    /**
     * 读取文件所有数据
     *
     * @param filePathName
     * @return map key和对应的值集合
     */
    public static Map<String, List<Object>> readAll(String filePathName) {
        if (StringUtils.isBlank(filePathName)) {
            return Maps.newHashMap();
        }
        if (filePathName.endsWith("csv")) {
            return readCsvAll(filePathName);
        }
        if (filePathName.endsWith("xlsx") || filePathName.endsWith("xls")) {
            return readExcelAll(filePathName);
        }
        return Maps.newHashMap();
    }

    public static Map<String, String> readFirstRow(String filePathName) {
        if (StringUtils.isBlank(filePathName)) {
            return Maps.newHashMap();
        }
        if (filePathName.endsWith("csv")) {
            return readCsvFirstRow(filePathName);
        }
        if (filePathName.endsWith("xlsx") || filePathName.endsWith("xls")) {
            return readExcelFirstRow(filePathName);
        }
        return Maps.newHashMap();
    }

    /**
     * 读取csv文件数据
     *
     * @param path
     * @return
     */
    public static Map<String, List<Object>> readCsvAll(String path) {
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

    /**
     * 获取csv文件第一行
     *
     * @param path
     * @return
     */
    public static Map<String, String> readCsvFirstRow(String path) {
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

    /**
     * 读取Excel文件第一行
     *
     * @param path
     * @return
     */
    public static Map<String, String> readExcelFirstRow(String path) {
        Map<String, String> dataMap = Maps.newHashMap();
        ExcelReader excelReader = ExcelUtil.getReader(path);
        List<Object> objs = excelReader.readRow(0);
        objs.stream().forEach(obj -> dataMap.put(Objects.toString(obj), ""));
        return dataMap;
    }

    /**
     * 读Excel文件
     *
     * @param path
     * @return
     */
    public static Map<String, List<Object>> readExcelAll(String path) {
        Map<String, List<Object>> dataMap = Maps.newHashMap();
        ExcelReader excelReader = ExcelUtil.getReader(path);
        // 获取第一行
        List<List<Object>> list = excelReader.read();
        // 将column和rows做一个映射
        Map<String, String> columnRowMap = Maps.newHashMap();
        for (int i = 0; i < list.size(); i++) {
            int index = 1;
            // 获取第一行
            List<Object> excelRow = list.get(i);
            Iterator it = excelRow.stream().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                // 表示为第一行，第一样是key,设置为column
                if (i == 0) {
                    String columnKey = String.valueOf(obj).trim();
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

    public static Long getFileCount(String path) {
        AtomicLong length = new AtomicLong(0);
        try {
            ExcelUtil.readBySax(path, 0, new RowHandler() {
                @Override
                public void handle(int i, long l, List<Object> list) {
                    length.incrementAndGet();
                }
            });
        } catch (Throwable e) {
            log.error("获取文件条数失败,{}", ExceptionUtils.getStackTrace(e));
        }
        return length.get();
    }
}
