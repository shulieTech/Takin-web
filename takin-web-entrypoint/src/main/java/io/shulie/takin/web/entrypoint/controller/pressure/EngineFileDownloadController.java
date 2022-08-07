package io.shulie.takin.web.entrypoint.controller.pressure;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.web.common.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/" + EntrypointUrl.MODULE_ENGINE)
@Slf4j
public class EngineFileDownloadController {

    @ApiOperation("引擎压测文件下载")
    @GetMapping(EntrypointUrl.METHOD_FILE_DOWNLOAD)
    public void download(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("文件不存在，地址：{}", filePath);
            return;
        }
        CommonUtil.zeroCopyDownload(file, response);
    }
}
