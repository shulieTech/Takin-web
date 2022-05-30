package io.shulie.takin.web.entrypoint.controller;

import javax.annotation.Resource;

import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.job.PressureEnvInspectionJob;
import io.shulie.takin.web.common.constant.ApiUrls;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "sys")
public class PressureEnvController {

    @Resource
    private RedisClientUtil redisClientUtil;

    @GetMapping("pressure/state")
    public ResponseResult<String> pressureState() {
        String message = redisClientUtil.getString(PressureEnvInspectionJob.SCHEDULED_PRESSURE_ENV_KEY);
        if (StringUtils.isBlank(message)) {
            return ResponseResult.success();
        }
        return ResponseResult.success(message);
    }

}
