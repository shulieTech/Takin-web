package io.shulie.takin.cloud.biz.service.cloud.server.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.annotation.Resource;

import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-05-14 17:59
 */

@Service
@Slf4j
public class FileWriteService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${script.path}")
    private String filePath;

    /**
     * 获取字节起始位置
     *
     * @param fileSceneUniqueKey -
     * @param requirePos         -
     * @return -
     */
    public synchronized Long calculateStartPos(String fileSceneUniqueKey, long requirePos) {
        //每次获取新位置
        String dbPos = stringRedisTemplate.opsForValue().get(fileSceneUniqueKey);
        long startPos = 0L;
        if (dbPos != null) {startPos = Long.parseLong(dbPos);}
        return startPos;

    }

    public void write(Long startPos, String filename, Long sceneId, byte[] bytes) throws Exception {
        if (StringUtils.isBlank(filename) || sceneId == null || bytes == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(sceneId);
        builder.append("-");
        builder.append(filename);
        /*
         * 起始位置不为空则将数据写入指定的pos，否则是多线程、多客户端上传，计算出新的起始位置
         */
        if (startPos == null) {
            startPos = calculateStartPos(builder.toString(), bytes.length);
        }
        /*
         * 抢占字节数组占用的位置
         */
        stringRedisTemplate.opsForValue().increment(builder.toString(), bytes.length);
        String path = filePath + SceneManageConstant.FILE_SPLIT + sceneId + SceneManageConstant.FILE_SPLIT + filename;

        if (!exitFile(path)) {
            createFile(path, (long)bytes.length);
        }
        writeByte(path, bytes, startPos, null);

    }

    public void writeByte(String filePath, byte[] bytes, Long startPos, Integer fileSize) throws IOException {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(filePath, "rw");
            file.seek(startPos);
            file.write(bytes);
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }

    }

    /**
     * 创建特定大小的空文件
     *
     * @param filepath    -
     * @param sizeInBytes -
     * @return -
     * @throws IOException -
     */
    private File createFile(String filepath, final Long sizeInBytes) throws IOException {

        File file = new File(filepath);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdir();
            }
            file.createNewFile();
        }
        return file;
    }

    public boolean exitFile(String path) {
        File file = new File(path);
        return file.exists();
    }

}
