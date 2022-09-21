package io.shulie.takin.web.biz.service.pressureresource.common;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MockInfo;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/21 11:45 AM
 */
public class RemoteCallUtil {
    /**
     * 转换
     * "label": "未配置",
     * "value": "0",
     * "label": "白名单",
     * "value": "1",
     * "label": "Groovy脚本mock",
     * "value": "2",
     * "label": "转发mock",
     * "value": "3",
     * "label": "返回值mock",
     * "value": "4",
     *
     * @return
     */
    public static Integer getType(PressureResourceRelateRemoteCallEntity call) {
        if (call == null) {
            return 0;
        }
        if (call.getPass() == null) {
            return 0;
        }
        if (call.getPass().intValue() == PassEnum.PASS_NO.getCode()) {
            return 0;
        }
        // 假如是通过,而且mock有值的话，判断下是什么类型
        if (call.getPass().intValue() == PassEnum.PASS_YES.getCode()) {
            if (StringUtils.isBlank(call.getMockReturnValue())) {
                return 1; // 白名单
            }
            if (StringUtils.isNotBlank(call.getMockReturnValue())) {
                MockInfo mockInfo = JSON.parseObject(call.getMockReturnValue(), MockInfo.class);
                // json格式
                if ("0".equals(mockInfo.getType())) {
                    return 4; // 返回值mock
                }
                if ("1".equals(mockInfo.getType())) {
                    return 2; // Groovy脚本mock
                }
            }
        }
        return 0;
    }
}
