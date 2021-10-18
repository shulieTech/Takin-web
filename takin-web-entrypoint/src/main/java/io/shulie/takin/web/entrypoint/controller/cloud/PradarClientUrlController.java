package io.shulie.takin.web.entrypoint.controller.cloud;

import java.util.HashMap;
import java.util.Map;

import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.swagger.annotations.Api;
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

    @GetMapping("download")
    public Response generateDownloadClientUrl() {
        Map<String, String> map = new HashMap<>(1);
        String cloudDomain = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_CLOUD_URL);
        String clientUri = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_REMOTE_CLIENT_DOWNLOAD_URI);
        map.put("url", cloudDomain + clientUri);
        return Response.success(map);
    }

}
