package io.shulie.takin.cloud.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import io.shulie.takin.cloud.common.constants.FileConstants;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据线程数量，分批读取文件
 *
 * @author xr.l
 */
@Slf4j
public class FileSliceByPodNum {
    private final int partSize;
    private final long fileLength;
    private RandomAccessFile rAccessFile;
    private final Set<StartEndPair> startEndPairs;

    private FileSliceByPodNum(File file, int partSize) {
        this.fileLength = file.length();
        this.partSize = partSize;
        try {
            this.rAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            log.error("异常代码【{}】,异常内容：文件读取异常 --> BigFileReaderUtil方法执行异常，异常信息: {}",
                TakinCloudExceptionEnum.FILE_READ_ERROR, e);
        }
        this.startEndPairs = new HashSet<>();
    }

    public ArrayList<StartEndPair> getStartEndPairs() {
        long everySize = this.fileLength / this.partSize;
        try {
            calculateStartEnd(0, everySize);
        } catch (IOException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
        }
        ArrayList<StartEndPair> pairList = new ArrayList<>(this.startEndPairs);
        pairList.sort(Comparator.comparing(StartEndPair::getStart));
        this.shutdown();
        return pairList;
    }

    private void calculateStartEnd(long start, long size) throws IOException {
        if (start > fileLength - 1) {
            return;
        }
        StartEndPair pair = new StartEndPair();
        pair.setStart(start);
        long endPosition = start + size - 1;
        if (endPosition >= fileLength - 1) {
            pair.setEnd(fileLength);
            startEndPairs.add(pair);
            return;
        }

        rAccessFile.seek(endPosition);
        byte tmp = (byte)rAccessFile.read();
        while (tmp != FileConstants.LINE_KEY && tmp != FileConstants.ENTER_KEY) {
            endPosition++;
            if (endPosition >= fileLength - 1) {
                endPosition = fileLength;
                break;
            }
            rAccessFile.seek(endPosition);
            tmp = (byte)rAccessFile.read();
        }
        //判断换行符是否为"\r\n"，即windows下的换行符CRLF，如果是，则将结束位置再+1
        rAccessFile.seek(endPosition + 1);
        tmp = (byte)rAccessFile.read();
        if (tmp == FileConstants.ENTER_KEY || tmp == FileConstants.LINE_KEY) {
            endPosition++;
        }
        pair.setEnd(endPosition);
        startEndPairs.add(pair);

        calculateStartEnd(endPosition + 1, size);

    }

    public void shutdown() {
        try {
            this.rAccessFile.close();
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件关闭异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_CLOSE_ERROR, e);
        }
    }

    public static class Builder {
        private int partSize = 1;
        private final File file;

        public Builder(String filepath) {
            this.file = new File(filepath);
            if (!this.file.exists()) {
                throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, "文件不存在！filepath:[" + filepath + "]");
            }
        }

        public Builder withPartSize(int size) {
            this.partSize = size;
            return this;
        }

        public FileSliceByPodNum build() {
            return new FileSliceByPodNum(this.file, this.partSize);
        }
    }

    public static class StartEndPair {
        private String partition;
        private long start;
        private long end;

        public String getPartition() {
            return partition;
        }

        public void setPartition(String partition) {
            this.partition = partition;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        @Override
        public String toString() {
            return "star=" + start + ";end=" + end + ";partition=" + partition;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int)(end ^ (end >>> 32));
            result = prime * result + (int)(start ^ (start >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            StartEndPair other = (StartEndPair)obj;
            if (end != other.end) {
                return false;
            }
            if (start != other.start) {
                return false;
            }
            return partition.equals(other.partition);
        }

    }
}
