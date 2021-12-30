package io.shulie.takin.web.biz.pojo.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 年度总结数据内容类
 * json 转换成的
 *
 * @author liuchuan
 * @date 2021/12/30 11:01 上午
 */
@ApiModel("出参类-年度总结数据内容类")
@Data
public class AnnualReportContentVO {

    @ApiModelProperty("开始日期")
    private String startDate;

    @ApiModelProperty("走过的天数, 单位 天")
    private Integer day;

    @ApiModelProperty("接入的业务活动数量(链路)")
    private Integer countActivity;

    @ApiModelProperty("接入的压测场景数量")
    private Integer countScene;

    @ApiModelProperty("最长链路经过应用数量")
    private Integer countOver;

    @ApiModelProperty("有最长应用链路的链路名称(业务活动名称)")
    private String longestActivity;

    @ApiModelProperty("压测总时长, 单位 分钟")
    private Integer totalPressureTime;

    @ApiModelProperty("最多压测时长的场景的时间")
    private Integer maxPressureTime;

    @ApiModelProperty("最多压测时长的场景名称")
    private String maxTimePressure;

    @ApiModelProperty("最多压测时长的场景的时间占用压测总时长的比例, 单位 %, 小数点两位")
    private BigDecimal pressureProportion;

    @JsonIgnore
    @ApiModelProperty("最晚的一次日期, 格式 2021-01-01 10:10:10")
    private LocalDateTime lastDateTime;

    @ApiModelProperty("最晚的一次日期, 格式 01-01")
    private String lastDate;

    @ApiModelProperty("最晚的一次时间, 格式 01:00:00")
    private String lastTime;

    @ApiModelProperty("优化系统提升占比, 单位 %, 小数点两位")
    private BigDecimal optimizedProportion;

    @ApiModelProperty("优化最厉害的业务活动名称")
    private String optimizedActivity;

    @ApiModelProperty("优化之前, 最大平均rt, 单位 毫秒")
    private BigDecimal beforeAvgRt;

    @ApiModelProperty("优化之后, 最小平均rt, 单位 毫秒")
    private BigDecimal afterAvgRt;

    @ApiModelProperty("平均rt优化的差值, 单位 毫秒")
    private BigDecimal diffAvgRt;

    @ApiModelProperty("平均rt最小的业务活动名称")
    private String minAvgRtActivity;

    @ApiModelProperty("平均rt最小的业务活动的消耗时间, 单位 毫秒")
    private BigDecimal minAvgRt;

}
