package io.shulie.takin.cloud.biz.config;

import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.service.CloudApiSenderServiceImpl;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.enums.deployment.DeploymentMethodEnum;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liyuanba
 */
@Configuration
@Data
public class AppConfig {

    @Value("${takin.web.url}")
    private String webUrl;
    @Value("${pressure.image.name:}")
    private String pressureEngineImage;
    /**
     * 部署方式
     */
    @Value("${tro.cloud.deployment.method:private}")
    private String deploymentMethod;
    @Deprecated
    @Value("${k8s.jvm.settings:-XX:MaxRAMPercentage=90.0 -XX:InitialRAMPercentage=90.0 -XX:MinRAMPercentage=90.0}")
    private String k8sJvmSettings;

    public DeploymentMethodEnum getDeploymentMethod() {
        return CommonUtil.getValue(DeploymentMethodEnum.PRIVATE, this.deploymentMethod, DeploymentMethodEnum::valueBy);
    }

    public String getCallbackUrl() {
        String url = DataUtils.mergeUrl(webUrl, EntrypointUrl.CALL_BACK_PATH);
        StringBuilder builder = new StringBuilder();
        ContextExt context = CloudPluginUtils.getContext();
        builder.append("?").append(CloudApiSenderServiceImpl.ENV_CODE).append("=").append(context.getEnvCode());
        builder.append("&").append(CloudApiSenderServiceImpl.TENANT_APP_KEY).append("=").append(context.getUserAppKey());
        builder.append("&").append(CloudApiSenderServiceImpl.USER_APP_KEY).append("=").append(context.getUserAppKey());
        url += builder.toString();
        return url;
    }
}
