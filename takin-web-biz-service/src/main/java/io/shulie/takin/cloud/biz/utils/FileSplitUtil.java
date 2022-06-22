/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.biz.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import io.shulie.takin.cloud.biz.pojo.FileSplitInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author liyuanba
 * @date 2021/10/12 5:23 下午
 */
@Slf4j
public class FileSplitUtil {

    /**
     * 根据文件中的partition信息自动分割文件，要求文件中的partition是整块在一起，没有乱的
     *
     * @param file           文件地址
     * @param partitionSplit 文件一行信息中信息分割符（正侧）
     * @return 返回各个partition的分片信息
     */
    public static List<FileSplitInfo> splitFileByPartition(String file, String partitionSplit) {
        if (StringUtils.isBlank(file)) {
            return null;
        }
        File f = new File(file);
        if (!f.exists() || !f.isFile()) {
            return null;
        }
        return splitFileByPartition(f, partitionSplit);
    }

    /**
     * 根据文件中的partition信息自动分割文件，要求文件中的partition是整块在一起，没有乱的
     *
     * @param file           文件
     * @param partitionSplit 文件一行信息中信息分割符（正侧）
     * @return 返回各个partition的分片信息
     */
    public static List<FileSplitInfo> splitFileByPartition(File file, String partitionSplit) {
        if (null == file) {
            return null;
        }
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile(file, "r");
            String text = null;
            long start = 0L;
            long end = reader.length();
            //排除文件头的空行
            do {
                text = readNextLine(reader, start);
                if (StringUtils.isBlank(text)) {
                    start++;
                }
            } while (StringUtils.isBlank(text) && start < end);
            int partition = getPartition(text, partitionSplit);
            //获取partition信息错误，可能文件格式不对
            if (partition == -1) {
                return null;
            }
            List<FileSplitInfo> result = Lists.newArrayList();

            Line line = readLine(reader, end, 0, end);
            int partitionEnd = getPartition(line.getText(), partitionSplit);
            if (partition == partitionEnd) {
                result.add(new FileSplitInfo(0L, end));
                return result;
            }
            do {
                line = findCurrentPartitionLastLine(reader, start, end, partition, partitionSplit);
                if (null == line || StringUtils.isBlank(line.getText())) {
                    return result;
                }
                result.add(new FileSplitInfo(start, line.getEndPos()));
                start = line.getEndPos();
                text = readNextLine(reader, start);
                partition = getPartition(text, partitionSplit);
            } while (start < end);

            return result;
        } catch (FileNotFoundException e) {
            log.error("文件不存在：file=" + file.getAbsolutePath(), e);
        } catch (IOException e) {
            log.error("文件读取错误：file=" + file.getAbsolutePath(), e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("file reader close error!file=" + file.getAbsolutePath(), e);
                }
            }
        }
        return null;
    }

    /**
     * 二分法查找partition的最后一行
     */
    public static Line findCurrentPartitionLastLine(RandomAccessFile reader, long start, long end, int partition,
        String partitionSplit) {
        long pos = start + (end - start) / 2;
        long lastPos = -1;
        do {
            Line line = readLine(reader, pos, start, end);
            if (null == line) {
                return null;
            }
            lastPos = pos;
            int p = getPartition(line.getText(), partitionSplit);
            if (partition != p) {//如果不相等，则在相等的在前面
                end = line.getStartPos();
                pos = start + (end - start) / 2;
            } else {//如果相等则可能已经找到，或者还在后面
                String nextText = null;
                do {
                    nextText = readNextLine(reader, line.getEndPos());
                    if (StringUtils.isBlank(nextText)) {
                        line.setEndPos(line.getEndPos() + 1);
                    }
                } while (StringUtils.isBlank(nextText) && line.getEndPos() < end);
                int np = getPartition(nextText, partitionSplit);
                if (p == np) {//如果相等该partition的最后一行还在后面
                    start = line.getEndPos();
                    pos = start + (end - start) / 2;
                } else {//不相等说明已经找到最后一行
                    return line;
                }
            }
        } while (start < end && pos > start && pos <= end && lastPos != pos);
        return null;
    }

    /**
     * 从行文本中提取partition值
     */
    public static Integer getPartition(String text, String split) {
        if (StringUtils.isBlank(text)) {
            return -1;
        }
        String[] splits = text.trim().split(split);
        return NumberUtils.toInt(splits[splits.length - 1], -1);
    }

    /**
     * 优先获取pos所在位置前面的那一行，如果没有则获取pos所在行
     */
    public static Line readLine(RandomAccessFile reader, long pos, long start, long end) {
        if (pos < start || pos > end) {
            return null;
        }
        Line line = readPreLine(reader, pos, start);
        if (null == line || StringUtils.isBlank(line.getText())) {
            long nextPos = pos;
            if (null != line && null != line.getEndPos()) {
                nextPos = line.getEndPos();
            } else {
                nextPos = start;
            }
            line = readCurrentPosLine(reader, nextPos, end);
        }
        return line;
    }

    /**
     * 读取pos位置前面最近的完整一行
     */
    public static Line readCurrentPosLine(RandomAccessFile reader, long pos, long end) {
        Line line = new Line();

        String text = readNextLine(reader, pos);
        line.setText(text);
        line.setStartPos(pos);
        long endPos = pos + text.length();

        //计算换行符
        while (endPos < end) {
            Byte b = readByte(reader, endPos);
            if (null == b || (b != 10 && b != 13)) {
                break;
            }
            endPos++;
        }
        line.setEndPos(endPos);

        return line;
    }

    /**
     * 读取pos位置前面最近的完整一行,如果pos前有2个不相连的换行符，则读取文本和标识文本开始和结束位置，如果只有1个换行符，则标识该位置，否则返回null
     */
    public static Line readPreLine(RandomAccessFile reader, long pos, long start) {
        long prePos = pos;
        Integer firstPos = null;
        Integer secondPos = null;
        byte[] lineData = null;
        Long lineStart = null;
        Long lineEnd = null;
        int page = 1;
        //如果1M内没有2个换行符，则读2M, 最多读10M
        while (null == secondPos && prePos >= start && page <= 10) {
            int length = (int)Math.min(1024 * 1024 * page++, pos - start);
            prePos = Math.max(start, pos - length);
            byte[] bytes = readBytes(reader, prePos, length);
            if (null == bytes) {
                return null;
            }
            boolean lastIsLineChar = false;
            for (int i = length; i > 0; i--) {
                byte b = bytes[i - 1];
                if (b != 13 && b != 10) {
                    lastIsLineChar = false;
                    continue;
                }
                //防止连续多个换行符
                if (lastIsLineChar) {
                    continue;
                }
                lastIsLineChar = true;
                if (null == firstPos) {
                    firstPos = i;
                    continue;
                } else {
                    secondPos = i;
                    break;
                }
            }
            if (null == secondPos && null != firstPos && prePos <= start) {
                secondPos = 0;
            }

            if (null != secondPos) {
                lineStart = prePos + secondPos;
                lineData = Arrays.copyOfRange(bytes, secondPos, firstPos);
            }
            if (null != firstPos) {
                lineEnd = prePos + firstPos;
            }
        }
        return new Line(lineData, lineStart, lineEnd);
    }

    /**
     * 从pos位置开始读到分行符位置结束
     */
    public static String readNextLine(RandomAccessFile reader, long pos) {
        try {
            reader.seek(pos);
            return reader.readLine();
        } catch (IOException e) {
            log.error("文件读取行失败：pos=" + pos, e);
        }
        return null;
    }

    /**
     * 从pos位置开始读1个字符
     */
    public static Byte readByte(RandomAccessFile reader, long pos) {
        try {
            reader.seek(pos);
            return reader.readByte();
        } catch (IOException e) {
            log.error("文件读取字节失败：pos=" + pos, e);
        }
        return null;
    }

    /**
     * 读取一定长度的内容
     */
    public static byte[] readBytes(RandomAccessFile reader, long pos, int length) {
        try {
            byte[] bytes = new byte[length];
            reader.seek(pos);
            reader.readFully(bytes);
            return bytes;
        } catch (IOException e) {
            log.error("文件读取失败,从" + pos + "位置读" + length + "个字符", e);
        }
        return null;
    }

    @Data
    public static class Line {
        private String text;
        private Long startPos;
        private Long endPos;

        public Line() {
        }

        public Line(byte[] data, Long startPos, Long endPos) {
            if (null != data) {
                this.text = new String(data);
            }
            this.startPos = startPos;
            this.endPos = endPos;
        }

        @Override
        public String toString() {
            return "Line{" +
                "text='" + text + '\'' +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                '}';
        }
    }
}
