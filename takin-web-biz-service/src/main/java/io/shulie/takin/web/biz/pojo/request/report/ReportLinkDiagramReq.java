package io.shulie.takin.web.biz.pojo.request.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author xjz@io.shulie
 * @date 2023/2/15
 * @desc 查询实况,报告链路图请求
 */
@Data
public class ReportLinkDiagramReq {
    
    @ApiModelProperty("场景Id")
    @NotNull(message = "sceneId不允许为空")
    private Long sceneId;
    
    @ApiModelProperty("报告id")
    private Long reportId;
    
    @NotBlank(message = "参数xpathMd5不允许为空")
    @ApiModelProperty("xpathMd5")
    private String xpathMd5;

    @NotNull(message = "参数startTime不允许为空")
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "参数endTime不允许为空")
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime; 
    
}
