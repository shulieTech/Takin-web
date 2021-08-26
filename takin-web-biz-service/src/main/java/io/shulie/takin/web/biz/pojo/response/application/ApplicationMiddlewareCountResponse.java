package io.shulie.takin.web.biz.pojo.response.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liuchuan
 * @date 2021/6/30 4:36 下午
 */
@ApiModel("出参类-统计出参")
@Setter
public class ApplicationMiddlewareCountResponse {

    @Getter
    @ApiModelProperty("总数")
    private Integer totalCount = 0;

    @ApiModelProperty("已支持数")
    private Integer supportedCount;

    @ApiModelProperty("未知数")
    private Integer unknownCount;

    @ApiModelProperty("未支持数")
    private Integer notSupportedCount;

    @ApiModelProperty("无状态数")
    private Integer noneCount;

    @ApiModelProperty("无需支持数")
    private Integer noSupportRequiredCount;

    public Integer getSupportedCount() {
        return supportedCount == null ? 0 : supportedCount;
    }

    public Integer getUnknownCount() {
        return unknownCount == null  ? 0 : unknownCount;
    }

    public Integer getNotSupportedCount() {
        return notSupportedCount == null  ? 0 : notSupportedCount;
    }

    public Integer getNoneCount() {
        return noneCount == null  ? 0 : noneCount;
    }

    public Integer getNoSupportRequiredCount() {
        return noSupportRequiredCount == null  ? 0 : noSupportRequiredCount;
    }

}
