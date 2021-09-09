package io.shulie.takin.web.entrypoint.controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.open.resp.common.CommonInfosResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.amdb.util.HttpClientUtil;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.result.system.SystemInfoItemVo;
import io.shulie.takin.web.data.result.system.SystemInfoVo;
import io.shulie.takin.web.diff.api.common.CloudCommonApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统信息数据
 *
 * @author caijianying
 */
@Slf4j
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "sys")
@Api(tags = "接口: 系统信息")
public class SystemController {

    private static final String UI_VERSION_FILE = "version.json";

    @Autowired
    private Environment environment;

    @Autowired
    private CloudCommonApi commonApi;

    @Autowired
    private AmdbClientProperties properties;

    @Value("${agent.interactive.takin.web.url:http://127.0.0.1:10086/takin-web}")
    private String takinWebUrl;

    @ApiOperation("系统信息")
    @GetMapping()
    public SystemInfoVo index() {
        //产品版本信息
        SystemInfoItemVo versionInfo = buildProductVersionInfo();
        //产品配置信息
        SystemInfoItemVo confInfo = buildProductConfInfo();
        //个人信息
        SystemInfoItemVo selfInfo = buildSelfInfo();

        SystemInfoVo resultVo = new SystemInfoVo();
        resultVo.setItemVos(Lists.newArrayList(versionInfo, confInfo, selfInfo));
        return resultVo;
    }

    private SystemInfoItemVo buildProductVersionInfo() {
        CommonInfosResp data = null;
        CloudCommonInfoWrapperReq req = new CloudCommonInfoWrapperReq();
        ResponseResult<CommonInfosResp> infos = commonApi.getCloudConfigurationInfos(req);
        if (!Objects.isNull(infos)) {
            data = infos.getData();
            if (Objects.isNull(data)) {
                data = new CommonInfosResp();
            }
            if (!infos.getSuccess()) {
                ResponseResult.ErrorInfo error = infos.getError();
                log.error("cloud接口返回错误：{}", error.getMsg());
                //throw new takinException(ExceptionCode.HTTP_REQUEST_ERROR, error.getMsg());
            }
        }
        String version = this.getAmdbVersion();

        SystemInfoItemVo itemVo = new SystemInfoItemVo();
        itemVo.setTitle("产品版本信息");
        HashMap<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("takin版本", ifNull(environment.getProperty("takin.web.version")));
        dataMap.put("cloud版本", ifNull(data.getCloudVersion()));
        dataMap.put("流量引擎版本", ifNull(data.getPressureEngineVersion()));
        dataMap.put("前端版本", ifNull(this.getUiVersion()));
        dataMap.put("AMDB版本", version);

        itemVo.setDataMap(dataMap);
        return itemVo;
    }

    private SystemInfoItemVo buildProductConfInfo() {
        //        String takinPath = "http://" + getLocalIp() + ":" + environment.getProperty("server.port") + environment.getProperty("server
        //        .servlet.context-path");
        SystemInfoItemVo itemVo = new SystemInfoItemVo();
        itemVo.setTitle("产品配置信息");
        HashMap<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("zk地址", ifNull(environment.getProperty("takin.config.zk.addr")));
        //        dataMap.put("takin地址", ifNull(takinPath));
        dataMap.put("takin地址", ifNull(takinWebUrl));

        itemVo.setDataMap(dataMap);
        return itemVo;
    }

    private SystemInfoItemVo buildSelfInfo() {
        SystemInfoItemVo itemVo = new SystemInfoItemVo();
        itemVo.setTitle("个人信息");
        itemVo.setDataMap(WebPluginUtils.getSystemInfo());
        return itemVo;
    }

    private String ifNull(String str) {
        return StringUtil.isNotBlank(str) ? str : "";
    }

    private String getLocalIp() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:")
                                && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ip = "127.0.0.1";
            ex.printStackTrace();
        }
        return ip;
    }

    private String getAmdbVersion() {
        String amdbUrl = properties.getUrl().getAmdb() + AmdbClientProperties.VERSION_PATH;
        log.info("请求amdb地址：{}", amdbUrl);
        String responseEntity = HttpClientUtil.sendGet(amdbUrl);
        log.info("amdb返回：{}", responseEntity);
        JSONObject result = JSONObject.parseObject(responseEntity);
        if (!CollectionUtils.isEmpty(result) && StringUtil.isNotBlank(result.getString("version"))) {
            return result.getString("version");
        }
        return "";
    }

    private String getUiVersion() {
        String json = HttpClientUtil.sendGet(takinWebUrl.substring(0, takinWebUrl.lastIndexOf("/")) + "/tro/" + UI_VERSION_FILE);
        if (!StringUtil.isEmpty(json)) {
            JSONObject object = JSONObject.parseObject(json);
            if (!Objects.isNull(object)) {
                return object.getString("version");
            }
        }
        return "";
    }

}
