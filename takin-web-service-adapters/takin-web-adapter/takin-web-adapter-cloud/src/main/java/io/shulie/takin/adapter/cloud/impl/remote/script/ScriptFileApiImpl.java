package io.shulie.takin.adapter.cloud.impl.remote.script;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.entrypoint.script.ScriptFileApi;
import io.shulie.takin.adapter.api.model.request.script.ScriptVerifyRequest;
import io.shulie.takin.adapter.api.service.CloudApiSenderService;
import io.shulie.takin.cloud.model.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScriptFileApiImpl implements ScriptFileApi {

    @Resource
    private CloudApiSenderService cloudApiSenderService;

    @Override
    public void verify(ScriptVerifyRequest request) {
        String data = cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_SCRIPT, EntrypointUrl.METHOD_SCRIPT_CHECK),
            request, new TypeReference<ApiResult<String>>() {}).getData();
        log.error("脚本文件校验结果: {}", data);
    }
}
