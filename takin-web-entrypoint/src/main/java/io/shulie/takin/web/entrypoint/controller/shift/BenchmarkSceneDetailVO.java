package io.shulie.takin.web.entrypoint.controller.shift;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @Author: liyuanba
 * @Date: 2022/4/6 11:48 上午
 */
@Data
public class BenchmarkSceneDetailVO extends BenchmarkSceneVO {
    @ApiModelProperty("基准测试用到的参数")
    private List suiteArgs;

    private List demandIds;

    private String versionId;
}
