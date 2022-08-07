package io.shulie.takin.adapter.cloud.impl.remote.script;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.entrypoint.script.ScriptFileApi;
import io.shulie.takin.adapter.api.model.request.pressure.ScriptAnnounceRequest;
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
        cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_RRESSURE, EntrypointUrl.METHOD_FILE_CHECK),
            request, new TypeReference<ApiResult<String>>() {}).getData();
    }

    @Override
    public void fileAnnounce(ScriptAnnounceRequest request) {
        cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_RRESSURE, EntrypointUrl.METHOD_FILE_ANNOUNCE),
            request, new TypeReference<ApiResult<Void>>() {}).getData();
    }
}
