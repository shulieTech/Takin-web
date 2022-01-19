package io.shulie.takin.web.biz.pojo.request.application;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationListByUpgradeRequest extends PageBaseDTO {

    /**
     * 不是表id
     * 防止前端long类型精度缺失, 使用字符串
     */
    private List<Long> appIds;


    private String applicationName;


}
