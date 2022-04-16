package io.shulie.takin.cloud.biz.output.scene.manage;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import io.shulie.takin.cloud.data.result.scenemanage.BusinessActivityDetailResult;
import io.shulie.takin.cloud.data.result.scenemanage.ScriptDetailResult;
import io.shulie.takin.cloud.data.result.scenemanage.SlaDetailResult;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/5/18 下午8:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneDetailOutput extends ContextExt {

    private Long id;

    private String sceneName;

    private String updateTime;

    private String lastPtTime;

    private EnumResult status;

    private Integer concurrenceNum;

    private Integer ipNum;

    private TimeBean pressureTestTime;

    private EnumResult pressureMode;

    private TimeBean increasingTime;

    private Integer step;

    private BigDecimal estimateFlow;

    private List<BusinessActivityDetailResult> businessActivityConfig;

    private List<ScriptDetailResult> uploadFile;

    private List<SlaDetailResult> stopCondition;

    private List<SlaDetailResult> warningCondition;

    public static void main(String[] args) {
        SceneDetailOutput dto = new SceneDetailOutput();
        dto.setBusinessActivityConfig(Collections.singletonList(new BusinessActivityDetailResult()));
        dto.setUploadFile(Collections.singletonList(new ScriptDetailResult()));
        dto.setStopCondition(Collections.singletonList(new SlaDetailResult()));
        dto.setWarningCondition(Collections.singletonList(new SlaDetailResult()));
        System.out.println(JSON.toJSONString(dto, SerializerFeature.WriteMapNullValue));
    }
}
