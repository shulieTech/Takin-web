package io.shulie.takin.web.entrypoint.controller.cloud;

import java.util.HashMap;
import java.util.Map;

import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.util.ConfigServerHelper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mubai
 * @date 2020-05-20 15:44
 */

@RequestMapping("/api/cloud/client/")
@RestController
@Api(tags = "客户端下载地址接口")
public class PradarClientUrlController {

    @Value("${remote.client.download.uri}")
    private String clientUri;

    /**
     * 获取下载地址
     *
     * @return
     */
    @GetMapping("download")
    public Response generateDownloadClientUrl() {
        String cloudDomain = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_CLOUD_URL);

        if (clientUri != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(cloudDomain);
            builder.append(clientUri);
            Map<String, String> map = new HashMap<>();
            map.put("url", builder.toString());
            return Response.success(map);
        }

        return Response.fail("缺少配置客户端的下载路径");
    }

}
