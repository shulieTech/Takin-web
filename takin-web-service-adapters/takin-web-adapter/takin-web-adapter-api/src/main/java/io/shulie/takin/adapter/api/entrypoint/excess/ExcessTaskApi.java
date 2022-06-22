package io.shulie.takin.adapter.api.entrypoint.excess;

import io.shulie.takin.adapter.api.model.request.excess.DataCalibrationRequest;

public interface ExcessTaskApi {

    Long dataCalibration(DataCalibrationRequest request);
}
