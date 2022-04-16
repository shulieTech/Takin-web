package io.shulie.takin.adapter.cloud.impl.remote.excess;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.entrypoint.excess.ExcessTaskApi;
import io.shulie.takin.adapter.api.model.request.excess.DataCalibrationRequest;
import io.shulie.takin.adapter.api.service.CloudApiSenderService;
import io.shulie.takin.cloud.model.response.ApiResult;
import org.springframework.stereotype.Service;

@Service
public class ExcessTaskApiImpl implements ExcessTaskApi {

    @Resource
    private CloudApiSenderService cloudApiSenderService;

    @Override
    public Long dataCalibration(DataCalibrationRequest request) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_EXCESS, EntrypointUrl.METHOD_DATA_CALIBRATION),
            request, new TypeReference<ApiResult<Long>>() {}).getData();
    }
}
