package io.shulie.takin.adapter.api.entrypoint.pressure;

import java.util.List;

import io.shulie.takin.adapter.api.constant.ThreadGroupType;
import io.shulie.takin.adapter.api.model.request.excess.DataCalibrationRequest;
import io.shulie.takin.adapter.api.model.request.pressure.PressureParamModifyReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureParamsReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStartReq;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStartReq.ThreadConfigInfo;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStopReq;
import lombok.Data;

public interface PressureTaskApi {

    Long start(PressureTaskStartReq req);

    String stop(PressureTaskStopReq req);

    void modifyParam(PressureParamModifyReq req);

    List<JobConfig> params(PressureParamsReq req);

    Long dataCalibration(DataCalibrationRequest request);

    @Data
    class JobConfig {
        private Long pressureId;
        private String ref;
        private ThreadGroupType type;
        private ThreadConfigInfo context;
    }
}
