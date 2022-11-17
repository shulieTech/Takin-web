package io.shulie.takin.web.biz.service.pressureresource.common;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MockInfo;
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
    public static Integer getType(String mockReturnValue, Integer pass) {
        // 优先匹配mock 假如存在mock的话,判断是什么mock值
        if (StringUtils.isNotBlank(mockReturnValue)) {
            MockInfo mockInfo = JSON.parseObject(mockReturnValue, MockInfo.class);
            // json格式
            if ("0".equals(mockInfo.getType())) {
                return 4; // 返回值mock
            }
            if ("1".equals(mockInfo.getType())) {
                return 2; // Groovy脚本mock
            }
        }
        // 有设置是否通过,而且是未通过的情况，且mock没有值,则为未配置
        if (pass != null && pass == PassEnum.PASS_YES.getCode()) {
            return 1; // 白名单
        }
        return 0; // 未配置
    }
}