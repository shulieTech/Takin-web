package io.shulie.takin.web.entrypoint.controller;

import java.io.IOException;
import java.util.Map;

import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Value("${page.report.takin}")
    private String pageTakin;
    @Value("${page.report.bench}")
    private String pageBench;

    @GetMapping("/parsepage")
    public void parsePage(HttpServletRequest req, HttpServletResponse response) {
        String baseURL = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

        String id = req.getParameter("id");
        try {
            if(id.startsWith("web-")){
                response.sendRedirect(pageTakin+"?id="+id.replace("web-", ""));
            }else if(id.startsWith("bench-")){
                response.sendRedirect(pageBench+"?reportId="+id.replace("bench-", ""));
            }else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
