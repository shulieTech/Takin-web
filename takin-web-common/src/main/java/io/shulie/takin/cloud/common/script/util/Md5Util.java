package io.shulie.takin.cloud.common.script.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author liyuanba
 * @date 2021/10/20 1:45 下午
 */
@Slf4j
public class Md5Util {
    /**
     * 区分大文件和一般文件界线:500M
     */
    public static final long BIG_FILE_SIZE = 1024 * 1024 * 500L;
    /**
     * 大文件采样分片数:100个片段
     */
    public static final int BIG_FILE_PART = 100;
    /**
     * 每个采样分片数大小：每个片段取1M
     */
    public static final int PART_SIZE = 1024 * 1024;

    public static void main(String[] args) {
        //        String  file = "/Users/liyuanba/Downloads/data 2.csv";
        String file = "/Users/liyuanba/Downloads/data.csv";
        System.out.println("fileSize=" + new File(file).length());
        System.out.println("md5=" + md5(file));
        long t = System.currentTimeMillis();
        System.out.println("file md5=" + md5File(file));
        System.out.println("t=" + (System.currentTimeMillis() - t));
    }

    /**
     * 字符串md5
     */
    public static String md5(String text) {
        return DigestUtils.md5Hex(text);
    }

    /**
     * 文件MD5，支持超大文件
     */
    public static String md5File(String file) {
        return md5File(new File(file));
    }

    /**
     * 文件MD5，支持超大文件
     */
    public static String md5File(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        long fileSize = file.length();
        if (fileSize >= BIG_FILE_SIZE) {
            return md5BigFile(file);
        } else {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                return md5File(in);
            } catch (IOException e) {
                log.error("md5 file error!file=" + file.getAbsolutePath(), e);
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("md5File inputstream close error!!file=" + file.getAbsolutePath(), e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 超大文件md5:根据文件大小，文件最后修改时间，文件采用内容进行md5加密
     */
    public static String md5BigFile(File file) {
        FileInputStream fileInputStream = null;
        try {
            long fileSize = file.length();
            long per = fileSize / BIG_FILE_PART;
            String fileInfo = file.lastModified() + "|" + fileSize + "|";
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            MD5.update(fileInfo.getBytes());
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[PART_SIZE];
            for (int i = 1; i <= BIG_FILE_PART; i++) {
                long start = i == BIG_FILE_PART ? (fileSize - per * i) + per : per;
                fileInputStream.skip(start - PART_SIZE);
                int length = fileInputStream.read(buffer);
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            log.error("md5 big file error!!file=" + file.getAbsolutePath(), e);
            return null;
        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                log.error("fileInputStream close error!!file=" + file.getAbsolutePath(), e);
            }
        }
    }

    public static String md5File(InputStream in) {
        if (null == in) {
            return null;
        }
        try {
            return DigestUtils.md5Hex(in);
        } catch (IOException e) {
            log.error("md5File error!!", e);
        }
        return null;
    }
}
