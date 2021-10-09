package io.shulie.takin.web.biz.utils.fastagentaccess;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description http response传输文件工具类
 * @Author ocean_wll
 * @Date 2021/8/17 11:05 上午
 */
@Slf4j
public class ResponseFileUtil {

    /**
     * 将文件通过response传输出去
     *
     * @param file       文件对象
     * @param delete     传输完以后是否删除，true：删除，false：不删除
     * @param fileName   文件名：不指定则用默认的
     * @param needHeader 是否需要指定的head响应头
     * @param response   response对象
     */
    public static void transfer(File file, boolean delete, String fileName, boolean needHeader,
        HttpServletResponse response) {
        if (file == null || !file.exists()) {
            return;
        }
        if (StringUtils.isBlank(fileName)) {
            fileName = file.getName();
        }

        try {
            // 响应头设置
            response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
            response.setHeader("Pragma", "no-cache");

            response.setHeader("Content-Disposition", String.format("attachment;filename=%s", fileName));
            response.setHeader("Expires", "0");

            // 使用sendfile:读取磁盘文件，并网络发送
            ServletOutputStream servletOutputStream = response.getOutputStream();
            FileChannel channel = new FileInputStream(file).getChannel();
            response.setHeader("Content-Length", String.valueOf(channel.size()));

            if (needHeader) {
                response.setHeader("Content-Type", "application/octet-stream");
                response.setHeader("Content-Transfer-Encoding", "chunked");
            }

            channel.transferTo(0, channel.size(), Channels.newChannel(servletOutputStream));
        } catch (Exception e) {
            log.error("file：{}，传输异常 --> 错误: {}", file.getName(), e.getMessage(), e);
        } finally {
            // 如果需要删除文件，则在传输完以后进行删除
            if (delete) {
                try {
                    file.delete();
                } catch (SecurityException e) {
                    file.deleteOnExit();
                }
            }
        }
    }
}
