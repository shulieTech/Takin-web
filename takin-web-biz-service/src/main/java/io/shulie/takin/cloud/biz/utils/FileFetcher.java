package io.shulie.takin.cloud.biz.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * -
 *
 * @author -
 */
public class FileFetcher {

    private final static int BUFFER_SIZE = 256;
    private final static char DEFAULT_SEPARATOR = '\n';
    private final FileChannel fc;
    private final char separator;
    private final File file;
    private final RandomAccessFile randomAccessFile;
    private ByteBuffer readBuffer;
    private volatile long readSize;
    private final long begin;
    private final static byte[] EMPTY = new byte[0];

    public FileFetcher(File file) throws FileNotFoundException {
        this(file, DEFAULT_SEPARATOR);
    }

    public FileFetcher(File file, char separator) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, "r");
        this.fc = randomAccessFile.getChannel();
        this.file = file;
        this.separator = separator;
        this.readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        if (file.getName().indexOf('.') != -1) {
            String suffix = file.getName().substring(file.getName().lastIndexOf('.') + 1);
            if (NumberUtils.isDigits(suffix)) {
                this.begin = Long.parseLong(suffix);
            } else {
                this.begin = 0;
            }
        } else {
            this.begin = 0;
        }
    }

    public long getBegin() {
        return begin;
    }

    public String getName() {
        return file.getName();
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public FileChannel getFc() {
        return fc;
    }

    public File getFile() {
        return file;
    }

    public byte[] read(long begin, long maxLength) throws IOException {
        /**
         *  换算成当前文件的开始位点，如果发现需要读取位点已经超过该文件的范围则返回空
         */
        long start = begin - getBegin();
        if (start < 0) {
            return EMPTY;
        }
        if (start > fc.size()) {
            return EMPTY;
        }
        /**
         * 当前文件剩余可读取的字节数
         */
        long availableBytes = fc.size() - start;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int offset = 0;
        while (true) {
            readBuffer.clear();
            /**
             * 定位开始位点
             */
            fc.position(start + offset);
            int bytesRead = fc.read(readBuffer);
            if (bytesRead <= 0) {
                break;
            }

            /**
             * 如果读取已经读取到最大长度
             */
            if (offset >= maxLength) {
                /**
                 * 找到换行符
                 */
                int enterIndex = enterOffset(readBuffer);
                if (enterIndex != -1) {
                    readBuffer.flip();
                    byte[] data = new byte[enterIndex + 1];
                    readBuffer.get(data, 0, enterIndex + 1);
                    bos.write(data, 0, data.length);
                    break;
                } else {
                    readBuffer.flip();
                    int length = readBuffer.limit();
                    byte[] data = new byte[length];
                    readBuffer.get(data, 0, length);
                    bos.write(data, 0, data.length);
                }
            } else if (offset >= availableBytes) {
                readBuffer.flip();
                int length = readBuffer.limit();
                byte[] data = new byte[length];
                readBuffer.get(data, 0, length);
                bos.write(data, 0, data.length);
                break;
            } else {
                readBuffer.flip();
                int length = readBuffer.limit();
                byte[] data = new byte[length];
                readBuffer.get(data, 0, length);
                bos.write(data, 0, data.length);
            }
            offset += BUFFER_SIZE;
        }
        byte[] data = bos.toByteArray();
        if (data.length == 0) {
            return data;
        }
        if (data[data.length - 1] == (byte)separator) {
            return data;
        }

        int lastIndex = -1;
        for (int i = data.length - 1; i >= 0; i--) {
            if (data[i] == (byte)separator) {
                lastIndex = i;
                break;
            }
        }
        /**
         * 如果未找到换行符,则返回空，说明数据并没有写完
         * 防止将非一行的数据读取给到服务端造成数据解析错误
         */
        if (lastIndex == -1) {
            return EMPTY;
        }

        byte[] newData = new byte[lastIndex + 1];
        System.arraycopy(data, 0, newData, 0, lastIndex + 1);
        return newData;
    }

    public long getLen(long begin, long bufferSize) throws IOException {
        long start = begin - getBegin();
        if (start < 0) {
            return 0;
        }
        if (start > fc.size()) {
            return 0;
        }
        long available = fc.size() - start;
        if (available > bufferSize) {
            while (true) {
                //一次性查询256个字节里面是不是有换行符
                //首先定位到position位置,再读256字节,从,256字节中读取到\n的偏移量
                readBuffer.clear();
                fc.position(start + bufferSize);
                int bytesRead = fc.read(readBuffer);
                if (bytesRead <= 0) {
                    available = bufferSize;
                    break;
                }
                int enterIndex = enterOffset(readBuffer);
                if (enterIndex != -1) {
                    available = bufferSize + enterIndex;
                    break;
                }
                bufferSize += 256;
            }
        }
        return available;
    }

    public void close() throws IOException {
        fc.close();
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

    /**
     * 定位换行符的位置，返回偏移量
     *
     * @param buffer
     * @return -
     */
    private int enterOffset(ByteBuffer buffer) {
        buffer.flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data, 0, buffer.limit());
        int len = data.length;
        for (int i = 0; i < len; i++) {
            if (data[i] == (byte)separator) {
                return i;
            }
        }
        return -1;
    }

    public void readSize(long readSize) {
        this.readSize += readSize;
    }

    public long readSize() {
        return readSize;
    }
}
