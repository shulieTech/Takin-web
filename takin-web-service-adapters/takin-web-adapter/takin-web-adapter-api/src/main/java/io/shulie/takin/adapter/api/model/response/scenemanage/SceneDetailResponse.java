package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.math.BigDecimal;
import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.EnumResult;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/5/18 下午8:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneDetailResponse extends ContextExt {

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

    private List<BusinessActivityDetailResp> businessActivityConfig;

    private List<ScriptDetailResponse> uploadFile;

    private List<SlaDetailResponse> stopCondition;

    private List<SlaDetailResponse> warningCondition;

    @Data
    public static class ScriptDetailResponse {

        private String fileName;

        private String uploadTime;

        private EnumResult fileType;

        private Long uploadedData;

        private EnumResult isSplit;
    }

    @Data
    public static class SlaDetailResponse {

        private String ruleName;

        private String businessActivity;

        private String rule;

        private EnumResult status;
    }
}
