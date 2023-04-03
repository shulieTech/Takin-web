package io.shulie.takin.web.biz.service.pts;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.pojo.response.pts.JmeterFunctionResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PtsMain {

    private static final String PRESSURE_MODE = "\"mode\":";


    private static String parseReportPressureMode(String ptConfig) {
        if(StringUtils.isBlank(ptConfig)) {
            return null;
        }
        int pos = ptConfig.indexOf(PRESSURE_MODE);
        if(pos == -1) {
            return null;
        }
        return ptConfig.substring(pos + PRESSURE_MODE.length(), pos + PRESSURE_MODE.length() + 1);
    }

    public static void main(String[] args) {
        String string = "{\"duration\":5,\"podNum\":1,\"threadGroupConfigMap\":{\"7dae7383a28b5c45069b528a454d1164\":{\"estimateFlow\":25.0,\"mode\":3,\"rampUp\":5,\"rampUpUnit\":\"m\",\"steps\":5,\"threadNum\":5,\"type\":0}},\"unit\":\"m\"}";
        System.out.println(parseReportPressureMode(string));
    }
}
