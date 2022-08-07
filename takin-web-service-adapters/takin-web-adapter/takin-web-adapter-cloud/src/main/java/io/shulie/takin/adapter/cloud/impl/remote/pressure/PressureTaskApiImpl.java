package io.shulie.takin.adapter.cloud.impl.remote.pressure;

import java.util.List;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.model.request.excess.DataCalibrationRequest;
import io.shulie.takin.adapter.api.model.request.pressure.PressureParamModifyReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureParamsReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStartReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStopReq;
import io.shulie.takin.adapter.api.service.CloudApiSenderService;
import io.shulie.takin.cloud.model.response.ApiResult;
import org.springframework.stereotype.Service;

@Service
public class PressureTaskApiImpl implements PressureTaskApi {

    @Resource
    private CloudApiSenderService cloudApiSenderService;

    @Override
    public Long start(PressureTaskStartReq req) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_RRESSURE, EntrypointUrl.METHOD_RRESSURE_START),
            req, new TypeReference<ApiResult<Long>>() {}).getData();
    }

    @Override
    public String stop(PressureTaskStopReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_RRESSURE, EntrypointUrl.METHOD_RRESSURE_STOP),
            req, new TypeReference<ApiResult<String>>() {}).getData();
    }

    @Override
    public Long dataCalibration(DataCalibrationRequest request) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_RRESSURE, EntrypointUrl.METHOD_DATA_CALIBRATION),
            request, new TypeReference<ApiResult<Long>>() {}).getData();
    }

    @Override
    public void modifyParam(PressureParamModifyReq req) {
        cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_RRESSURE, EntrypointUrl.METHOD_RRESSURE_MODIFY),
            req, new TypeReference<ApiResult<Void>>() {}).getData();
    }

    @Override
    public List<JobConfig> params(PressureParamsReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_RRESSURE, EntrypointUrl.METHOD_RRESSURE_PARAMS),
            req, new TypeReference<ApiResult<List<JobConfig>>>() {}).getData();
    }
}
