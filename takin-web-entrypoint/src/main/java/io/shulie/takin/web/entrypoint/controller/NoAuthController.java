package io.shulie.takin.web.entrypoint.controller;

import java.util.Map;

import io.shulie.takin.web.common.util.RedisClientUtils;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 无需权限的访问
 *
 * @author qianshui
 * @date 2020/11/23 下午6:18
 */
@RestController
@RequestMapping("/api/noauth")
public class NoAuthController {

    @Autowired
    private RedisClientUtils redisClientUtils;

    @PutMapping("/resume/scenetask")
    public ResponseResult resumeSceneTask(@RequestBody Map<String, Object> paramMap) {
        Long reportId = Long.parseLong(String.valueOf(paramMap.get("reportId")));
        if (reportId == null) {
            ResponseResult.fail("reportId cannot be null,", "");
        }
        redisClientUtils.del(WebRedisKeyConstant.REPORT_WARN_PREFIX + reportId);
        String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3, WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
            String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId));
        redisClientUtils.del(redisKey);
        return ResponseResult.success("resume success");
    }
}
