package io.shulie.takin.web.biz.pojo.output.scene;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/11 1:33 下午
 */
@Data
@ApiModel("出参类-场景报告列表出参")
public class SceneReportListOutput {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("横坐标, 当前查询时间, 举例: 15:15:24")
    private String time;

    @ApiModelProperty("纵坐标, 报告tps")
    private BigDecimal tps;

    @JsonIgnore
    private LocalDateTime datetime;

}
