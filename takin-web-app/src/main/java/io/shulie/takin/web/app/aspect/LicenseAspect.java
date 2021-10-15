package io.shulie.takin.web.app.aspect;

import java.util.Objects;

import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.common.domain.WebRequest;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.util.ConfigServerHelper;
import io.shulie.takin.web.ext.api.user.WebUserExtApi;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qianshui
 * @date 2020/5/14 下午8:47
 */
@Aspect
@Component
@Slf4j
public class LicenseAspect {

    @Autowired
    private PluginManager pluginManager;

    @Pointcut("execution(public * io.shulie.takin.web.common.http.HttpWebClient.request*(..))")
    public void setLicense() {

    }

    @Before("setLicense()")
    public void doBefore(JoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        if (params != null && params.length == 1) {
            if (params[0] instanceof WebRequest) {
                WebRequest inParam = (WebRequest)params[0];
                String remoteUrl = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_CLOUD_URL);
                inParam.setRequestUrl(remoteUrl + inParam.getRequestUrl());
                WebUserExtApi userExtApi = pluginManager.getExtension(WebUserExtApi.class);
                if (Objects.isNull(userExtApi)) {
                    return;
                }
                userExtApi.fillCloudUserData(inParam);
            }
        }
    }
}
