package io.shulie.takin.web.entrypoint.controller.shift;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
    public class AppSceneVO {

        private String dids;

        private String vid;

        @ApiModelProperty("id")
        private Long id;

        @ApiModelProperty("场景名称")
        private String sceneName;

        @ApiModelProperty("压测类型:0:自动摸高 1：手工设置")
        private String pressureType;

        @ApiModelProperty("压测时间")
        private Integer time;

        @ApiModelProperty("场景线程组")
        private List groupSetting;

        @ApiModelProperty("压测活动")
        private List sceneActivities;

        @ApiModelProperty("场景文件信息")
        private List sceneFiles;

        @ApiModelProperty("脚本线程组")
        private List jmxThreadGroup;

        @ApiModelProperty("脚本活动")
        private List jmxActivities;
    }