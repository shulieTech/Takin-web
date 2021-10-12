package io.shulie.takin.web.app.conf.intercepter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 抽象拦截器
 * 添加统一的拦截
 *
 * @author liuchuan
 * @date 2021/7/12 3:23 下午
 */
@Component
public class AbstractInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 404 处理
        if (Objects.equals(response.getStatus(), HttpStatus.NOT_FOUND.value())) {
            Map<String, Object> map = new HashMap<>(4);
            map.put("msg", "调用接口不存在, 请查看配置是否正确!");
            map.put("status", HttpStatus.NOT_FOUND.value());
            this.printResponse(response, JsonUtil.bean2Json(map), HttpStatus.NOT_FOUND.value());
            return false;
        }

        return true;
    }

    /**
     * 打印响应信息
     *
     * @param response     响应
     * @param responseJson 响应信息
     * @param responseCode 响应码
     * @throws IOException io 异常
     */
    void printResponse(HttpServletResponse response, String responseJson, Integer responseCode) throws IOException {
        // 处理 TAKIN_AUTHORITY 请求头
        {
            // 声明字符串
            String takinAuthorityHeaderName = "takin-authority";
            String accessControlExposeHeaderName = "Access-Control-Expose-Headers";
            // 填充请求头并对外暴露(Chrome安全策略)
            response.setHeader(takinAuthorityHeaderName, WebPluginUtils.checkUserPlugin().toString());
            Collection<String> headers = response.getHeaderNames();
            response.setHeader(accessControlExposeHeaderName, String.join(",", headers));
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(responseCode);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(responseJson);
        printWriter.flush();
        printWriter.close();
    }

}
