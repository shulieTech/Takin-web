package io.shulie.takin.cloud.biz.service.schedule;

import java.io.IOException;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mubai
 * @date 2020-10-29 17:15
 */
@Slf4j
public class CopyFileTask implements Runnable {
    private String source;
    private String dest;

    public CopyFileTask(String source, String dest) {
        this.source = source;
        this.dest = dest;
    }

    @Override
    public void run() {

        String cmd = "cmd /c copy" + source + " " + dest;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件复制失败 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_COPY_ERROR, e);
        }
    }
}
