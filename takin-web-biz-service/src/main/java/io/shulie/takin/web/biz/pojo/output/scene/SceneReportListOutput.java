package io.shulie.takin.web.biz.pojo.output.scene;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ApiModelProperty(value = "压测报告id")
    private Long reportId;


    @ApiModelProperty(value = "最大并发数")
    private Integer maxConcurrent;

    @ApiModelProperty(value = "压测开始时间")
    private String startTime;

}
