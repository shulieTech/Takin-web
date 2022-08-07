package io.shulie.takin.cloud.biz.config;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import cn.hutool.core.net.url.UrlBuilder;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.service.CloudApiSenderServiceImpl;
import io.shulie.takin.cloud.biz.service.schedule.impl.FileSplitService;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.biz.utils.FileTypeBusinessUtil;
import io.shulie.takin.cloud.common.enums.deployment.DeploymentMethodEnum;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import static io.shulie.takin.cloud.common.constants.SceneManageConstant.FILE_SPLIT;

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
    @Value("${data.path}")
    private String nfsDir;
    @Value("${script.path}")
    private String scriptPath;

    public DeploymentMethodEnum getDeploymentMethod() {
        return CommonUtil.getValue(DeploymentMethodEnum.PRIVATE, this.deploymentMethod, DeploymentMethodEnum::valueBy);
    }

    public String getCallbackUrl() {
        return getCallbackUrl(EntrypointUrl.CALL_BACK_PATH);
    }

    public String getEngineFileDownloadUrl(Map<String, String> param) {
        String url = getCallbackUrl(EntrypointUrl.ENGINE_FILE_DOWNLOAD);
        UrlBuilder builder = UrlBuilder.of(url, StandardCharsets.UTF_8);
        if (!CollectionUtils.isEmpty(param)) {
            param.forEach(builder::addQuery);
        }
        return builder.build();
    }

    public String getClusterRegisterUrl() {
        return UrlBuilder.of(DataUtils.mergeUrl(webUrl, EntrypointUrl.CLUSTER_REGISTER), StandardCharsets.UTF_8).build();
    }

    private String getCallbackUrl(String path) {
        ContextExt context = CloudPluginUtils.getContext();
        return UrlBuilder.of(DataUtils.mergeUrl(webUrl, path), StandardCharsets.UTF_8)
            .addQuery(CloudApiSenderServiceImpl.ENV_CODE, context.getEnvCode())
            .addQuery(CloudApiSenderServiceImpl.TENANT_APP_KEY, context.getUserAppKey())
            .addQuery(CloudApiSenderServiceImpl.USER_APP_KEY, context.getUserAppKey())
            .build();
    }

    public String reWritePathByNfsRelative(String filePath, Integer fileType, boolean absolutePath) {
        if (absolutePath) {
            String path = filePath.replaceFirst(nfsDir, "");
            if (path.startsWith(FILE_SPLIT)) {
                path = path.substring(1);
            }
            return path;
        }
        if (FileTypeBusinessUtil.isAttachment(fileType)) {
            filePath = FileSplitService.reWriteAttachmentPath(filePath);
        }
        String prefix = scriptPath.replaceAll(nfsDir, "");
        if (StringUtils.isBlank(prefix)) {
            return filePath;
        }
        if (prefix.startsWith(FILE_SPLIT)) {
            prefix = prefix.substring(1);
        }
        if (!prefix.endsWith(FILE_SPLIT)) {
            prefix += FILE_SPLIT;
        }
        return prefix + filePath;
    }
}
