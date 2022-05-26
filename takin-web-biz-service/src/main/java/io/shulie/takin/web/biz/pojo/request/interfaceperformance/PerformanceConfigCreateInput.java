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
    private PressureConfigRequest pressureConfigRequest  = PressureConfigRequest.DEFAULT;;

    // 临时调试使用,需要这里的参数信息
    private PerformanceParamDetailResponse dataFileDataRequest;

    /**
     * 结果Id
     */
    private String resultId;
}
