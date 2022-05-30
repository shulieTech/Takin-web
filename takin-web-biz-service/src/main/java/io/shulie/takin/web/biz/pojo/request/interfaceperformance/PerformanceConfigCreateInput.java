package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import io.shulie.takin.web.biz.pojo.request.scene.NewSceneRequest;
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
    // private PressureConfigRequest pressureConfigRequest  = PressureConfigRequest.DEFAULT;;
    //private NewSceneRequest pressureConfigRequest = NewSceneRequest.DEFAULT;

    /**
     * 结果Id
     */
    private String resultId;
}
