package io.shulie.takin.web.entrypoint.controller.shift;

import lombok.Data;

import java.time.LocalDateTime;

@Data
   public class PressureTaskResult {
        private Long id;
        /**
         * 任务ID
         */
        private Long taskId;
        /**
         * 场景ID
         */
        private Long sceneId;
        /**
         * 用户ID
         */
        private Long userId;
        /**
         * 压力机ID
         */
        private Long machineId;
        /**
         * 压力机信息
         */
        private String machine;
        /**
         * 基准测试名称
         */
        private String suite;
        /**
         * 测试结果，json字符串
         */
        private String testResult;
        /**
         * 测试开始时间
         */
        private LocalDateTime startTime;
        /**
         * 测试结束时间
         */
        private LocalDateTime endTime;

    }