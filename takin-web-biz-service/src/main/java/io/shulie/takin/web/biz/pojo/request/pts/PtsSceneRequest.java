package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author junshi
 * @ClassName PtsSceneRequest
 * @Description
 * @createTime 2023年03月15日 15:24
 */
@Data
@ApiModel("新增|修改业务流程入参")
public class PtsSceneRequest implements Serializable {

    @ApiModelProperty(value = "业务流程id，修改时必填")
    private Long id;

    @ApiModelProperty(value = "业务流程名称", required = true)
    @NotBlank(message = "业务流程名称不能为空")
    private String processName;

    @ApiModelProperty(value = "前置链路")
    private PtsPreLinkRequest preLink = new PtsPreLinkRequest();

    @ApiModelProperty(value = "全局默认Header")
    private PtsApiHeaderRequest globalHeader = new PtsApiHeaderRequest();

    @ApiModelProperty(value = "全局默认HTTP")
    private PtsGlobalHttpRequest globalHttp = new PtsGlobalHttpRequest();

    @ApiModelProperty(value = "计数器")
    private List<PtsCounterRequest> counters = new ArrayList<>();

    @ApiModelProperty(value = "串联链路", required = true)
    @NotNull(message = "串联链路不能为空")
    private List<PtsLinkRequest> links = new ArrayList<>();

    @ApiModelProperty(value = "数据源")
    private PtsDataSourceRequest dataSource = new PtsDataSourceRequest();
}
