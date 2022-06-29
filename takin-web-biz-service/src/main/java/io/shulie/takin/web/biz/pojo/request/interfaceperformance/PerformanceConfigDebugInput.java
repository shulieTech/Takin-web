package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import lombok.Data;

@Data
public class PerformanceConfigDebugInput extends InterfacePerformanceConfigVO {
    /**
     * contentType数据
     */
    private ContentTypeVO contentTypeVo;

    /**
     * 结果Id
     */
    private String resultId;
}
