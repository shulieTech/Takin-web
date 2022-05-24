package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import lombok.Data;

@Data
public class PerformanceConfigCreateInput extends InterfacePerformanceConfigVO {
    /**
     * contentType数据
     */
    private ContentTypeVO contentTypeVo;

    /**
     * 压测相关配置
     */
    private PressureConfigRequest pressureConfigRequest;
}
