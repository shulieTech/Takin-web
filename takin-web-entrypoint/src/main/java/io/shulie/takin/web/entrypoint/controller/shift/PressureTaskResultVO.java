package io.shulie.takin.web.entrypoint.controller.shift;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
   public class PressureTaskResultVO {
        @ApiModelProperty("本子报告执行时长")
        private Long runTime;

        @ApiModelProperty("主任务执行时长")
        private Long taskRunTime;

        private PressureMachineVO machineDetail;

        @ApiModelProperty("测试结果")
        private BenchmarkResultVO testResult;

        @ApiModelProperty("报告id")
        private Long id;
        /**
         * 任务ID
         */
        @ApiModelProperty("任务ID")
        private Long taskId;
        /**
         * 场景ID
         */
        @ApiModelProperty("场景ID")
        private Long sceneId;

        @ApiModelProperty("场景名称")
        private String sceneName;
        /**
         * 压力机ID
         */
        @ApiModelProperty("基准测试名称")
        private String suite;

        @ApiModelProperty("压力机ID")
        private Long machineId;
        /**
         * 压力机ID
         */
        @ApiModelProperty("压力机")
        private String machine;

        @ApiModelProperty("测试开始时间")
        private LocalDateTime startTime;

        @ApiModelProperty("测试结束时间")
        private LocalDateTime endTime;
    }