package io.shulie.takin.web.biz.service.pts;

import io.shulie.takin.web.biz.utils.LinuxHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
@EnableAsync
@Slf4j
public class PtsDebugAsync {

    @Async(value = "foreachQueryThreadPool")
    public void runJmeterCommand(String command) {
        AtomicReference<Process> shellProcess = new AtomicReference<>();
        LinuxHelper.runShell(command, 60L,
                process -> shellProcess.set(process),
                message -> {}
        );
    }
}
