package io.shulie.takin.web.entrypoint.controller.cloud;

import java.util.HashMap;
import java.util.Map;

import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
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

    @Value("${takin.cloud.url}")
    private String cloudUrl;

    @GetMapping("download")
    public Response generateDownloadClientUrl() {
        Map<String, String> map = new HashMap<>(1);
        String clientUri = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_REMOTE_CLIENT_DOWNLOAD_URI);
        map.put("url", cloudUrl + clientUri);
        return Response.success(map);
    }

}
