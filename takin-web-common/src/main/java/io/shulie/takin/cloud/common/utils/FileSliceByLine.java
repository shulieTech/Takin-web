package io.shulie.takin.cloud.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import io.shulie.takin.cloud.common.constants.FileConstants;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 大文件分片
 *
 * @author moriarty
 */
@Slf4j
public class FileSliceByLine {
    private final String filePath;
    private final String separator;
    private Long prePosition;
    private Integer nextPartitionNum;
    private final Integer orderColumnNum;
    private final Map<String, Integer> partitionMap;
    private final Map<Integer, FileSliceInfo> fileSliceInfoMap;

    private FileSliceByLine(String filePath, String separator, Integer columnNum) {
        this.filePath = filePath;
        this.separator = separator;
        this.fileSliceInfoMap = new TreeMap<>();
        this.prePosition = 0L;
        this.partitionMap = new HashMap<>();
        this.nextPartitionNum = 0;
        this.orderColumnNum = columnNum;
    }

    public Map<Integer, FileSliceInfo> sliceFile() throws TakinCloudException {
        BufferedReader reader = null;
        try {
            String fileEncoder = "UTF-8";
            File file = new File(this.filePath);
            int lineBreakSize = getLineBreakSize(file);
            reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.filePath), fileEncoder));
            reader.lines().filter(StringUtils::isNotBlank).forEach(
                line -> {
                    String[] values = line.split(this.separator);
                    String partition;
                    if (orderColumnNum == null || values.length - 1 > orderColumnNum) {
                        partition = values[values.length - 1];
                    } else {
                        partition = values[orderColumnNum];
                    }
                    Integer partitionNum = partitionMap.get(partition);
                    if (partitionNum != null) {
                        FileSliceInfo fileSliceInfo = this.fileSliceInfoMap.get(partitionNum);
                        fileSliceInfo.setEnd(fileSliceInfo.getEnd() + line.getBytes().length + lineBreakSize);
                        this.fileSliceInfoMap.put(partitionNum, fileSliceInfo);
                        prePosition = fileSliceInfo.end;
                    } else {
                        partitionMap.put(partition, nextPartitionNum);
                        FileSliceInfo fileSliceInfo = new FileSliceInfo();
                        fileSliceInfo.setPartition(nextPartitionNum);
                        fileSliceInfo.setStart(prePosition);
                        fileSliceInfo.setEnd(prePosition + line.getBytes().length + lineBreakSize);
                        fileSliceInfoMap.put(nextPartitionNum, fileSliceInfo);
                        prePosition = fileSliceInfo.end + 1;
                        nextPartitionNum++;
                    }
                }
            );
            Map<Integer, FileSliceInfo> resultMap = new HashMap<>(fileSliceInfoMap.size());
            FileSliceInfo sliceInfo;
            for (Map.Entry<Integer, FileSliceInfo> entry : fileSliceInfoMap.entrySet()) {
                sliceInfo = new FileSliceInfo() {{
                    setPartition(entry.getValue().getPartition());
                    setStart(entry.getValue().getStart());
                    if (entry.getKey().equals(nextPartitionNum - 1)) {
                        setEnd(file.length());
                    } else {
                        setEnd(entry.getValue().getEnd() - 1);
                    }
                }};
                resultMap.put(entry.getKey(), sliceInfo);
            }
            return resultMap;
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                log.error("异常代码【{}】,异常内容：文件关闭异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.FILE_CLOSE_ERROR, ex);
            }
        }
    }

    private int getLineBreakSize(File file) {
        BufferedReader reader = null;
        RandomAccessFile rAccessFile = null;
        try {
            reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.filePath), StandardCharsets.UTF_8));
            int lineLength = reader.readLine().length();
            rAccessFile = new RandomAccessFile(file, "r");
            rAccessFile.seek(lineLength);
            byte tmp = (byte)rAccessFile.read();
            while (tmp != FileConstants.ENTER_KEY && tmp != FileConstants.LINE_KEY) {
                rAccessFile.seek(++lineLength);
                tmp = (byte)rAccessFile.read();
            }
            rAccessFile.seek(++lineLength);
            tmp = (byte)rAccessFile.read();
            if (tmp == FileConstants.ENTER_KEY || tmp == FileConstants.LINE_KEY) {
                return 2;
            } else {
                return 1;
            }
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件关闭异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_READ_ERROR, e);
        } finally {
            try {
                if (rAccessFile != null) {
                    rAccessFile.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                log.error("异常代码【{}】,异常内容：文件关闭异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.FILE_CLOSE_ERROR, ex);
            }
        }
        return 1;
    }

    public static class Builder {
        private String separator;
        private Integer columnNum;
        private final String filePath;

        public Builder(String filepath) {
            this.filePath = filepath;
            File file = new File(this.filePath);
            if (!file.exists()) {
                throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, "文件不存在！filepath:[" + filepath + "]");
            }
        }

        public Builder withSeparator(String separator) {
            this.separator = separator;
            return this;
        }

        public Builder withOrderColumnNum(Integer columnNum) {
            this.columnNum = columnNum;
            return this;
        }

        public FileSliceByLine build() {
            return new FileSliceByLine(this.filePath, this.separator, this.columnNum);
        }
    }

    @Data
    public static class FileSliceInfo {
        private Integer partition;
        private Long start;
        private Long end;
    }
}
