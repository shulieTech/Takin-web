package io.shulie.takin.web.entrypoint.controller.shift;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author: liyuanba
 * @Date: 2022/3/16 10:21 上午
 */
@Data
public class BenchmarkSceneVO {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("压测状态 0：待启动 1：压测中")
    private Integer status;

    @ApiModelProperty("基准测试套件名称")
    private String suites;

    @ApiModelProperty("子任务数")
    private Integer subTaskNum;

    @ApiModelProperty("已完成的子任务数")
    private Integer finishedSubTaskNum;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime lastTestTime;

    @ApiModelProperty("当前正在测试中的任务ID")
    private Long taskId;
    @ApiModelProperty("是否关联了定时任务,1:关联了;0:没关联")
    private Integer hasRelatedScheduler;
}
