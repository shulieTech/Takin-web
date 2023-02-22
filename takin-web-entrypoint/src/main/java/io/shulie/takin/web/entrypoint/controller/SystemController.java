package io.shulie.takin.web.entrypoint.controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.adapter.api.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.adapter.api.model.response.common.CommonInfosResp;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.amdb.util.HttpClientUtil;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.sys.VersionService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.constant.BaseConfigConstant;
import io.shulie.takin.web.data.result.system.SystemInfoItemVo;
import io.shulie.takin.web.data.result.system.SystemInfoVo;
import io.shulie.takin.web.diff.api.common.CloudCommonApi;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
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
@RequestMapping(ApiUrls.TAKIN_API_URL + "sys")
@Api(tags = "接口: 系统信息")
public class SystemController {

    private static final String UI_VERSION_FILE = "version.json";

    @Value("${takin.web.version}")
    private String takinWebVersion;

    @Value("${takin.web.url}")
    private String takinWebUrl;

//    @Value("${takin.config.zk.addr}")
//    private String zkAddr;

    @Autowired
    private CloudCommonApi commonApi;

    @Autowired
    private AmdbClientProperties properties;

    @Autowired
    private BaseConfigService baseConfigService;

    @Resource
    private StrategyConfigService strategyConfigService;

    @Resource
    private AppConfig appConfig;

    @Resource
    private VersionService versionService;

    /**
     * 前端样式存储
     * @return
     */
    @ApiOperation("前端样式默认配置")
    @GetMapping("/front/config/get")
    public JSONObject getFrontConfig() {
        TBaseConfig tBaseConfig = baseConfigService.queryByConfigCode(BaseConfigConstant.DEFAULT_FRONT_CSS_CONFIG);
        if(tBaseConfig == null){
            return new JSONObject();
        }
        return JSON.parseObject(tBaseConfig.getConfigValue());
    }


    @ApiOperation("系统信息")
    @GetMapping()
    public SystemInfoVo index(String version) {
        //产品版本信息
        SystemInfoItemVo versionInfo = buildProductVersionInfo(version);
        //产品配置信息
        SystemInfoItemVo confInfo = buildProductConfInfo();
        //个人信息
        SystemInfoItemVo selfInfo = buildSelfInfo();
        // git版本信息
        SystemInfoItemVo gitVersions = buildGitVersion();

        SystemInfoVo resultVo = new SystemInfoVo();
        resultVo.setItemVos(Lists.newArrayList(versionInfo, confInfo, selfInfo, gitVersions));
        return resultVo;
    }

    private SystemInfoItemVo buildProductVersionInfo(String uiVersion) {
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
            }
        }
        String engineImage = appConfig.getPressureEngineImage();
        String engineVersion = engineImage;
        if (StringUtils.isBlank(engineImage)) {
            StrategyConfigExt config = strategyConfigService.getCurrentStrategyConfig();
            if (config != null) {
                engineVersion = config.getPressureEngineImage();
            }
        }
        String version = this.getAmdbVersion();

        SystemInfoItemVo itemVo = new SystemInfoItemVo();
        itemVo.setTitle("产品版本信息");
        HashMap<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("takin版本", ifNull(takinWebVersion));
        dataMap.put("cloud版本", ifNull(data.getVersion()));
        dataMap.put("流量引擎版本", ifNull(engineVersion));
        dataMap.put("前端版本", ifNull(this.getUiVersion(uiVersion)));
        dataMap.put("AMDB版本", version);

        itemVo.setDataMap(dataMap);
        return itemVo;
    }

    private SystemInfoItemVo buildProductConfInfo() {
        SystemInfoItemVo itemVo = new SystemInfoItemVo();
        itemVo.setTitle("产品配置信息");
        HashMap<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("zookeeper 地址", ifNull("没有使用zk"));
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
                            String ipaddress = inetAddress.getHostAddress();
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

    private String getUiVersion(String uiVersion) {
        String json = uiVersion;
        if (StringUtil.isBlank(json)) {
            //兼容以前的获取方式
            String webUrl = takinWebUrl.substring(0, takinWebUrl.lastIndexOf("/"));
            json = HttpClientUtil.sendGet(webUrl + "/tro/" + UI_VERSION_FILE);

            if (StringUtil.isBlank(json)) {
                json = HttpClientUtil.sendGet(webUrl + "/" + UI_VERSION_FILE);
            }
        }

        if (StringUtil.isNotBlank(json)) {
            JSONObject parseObject = JSONObject.parseObject(json);
            String version = parseObject.getString("version");
            if (StringUtil.isNotBlank(version)) {
                return version;
            }
        }

        return "";
    }

    private SystemInfoItemVo buildGitVersion() {
        SystemInfoItemVo gitVersions = new SystemInfoItemVo();
        gitVersions.setTitle("git版本信息");
        gitVersions.setDataMap(versionService.queryZkVersionData());
        return gitVersions;
    }

}
