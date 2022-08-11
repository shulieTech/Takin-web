package io.shulie.takin.web.biz.pojo.response.scene;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BenchmarkSuiteResponse implements Serializable {

    private Long id;
    /**
     * 基准测试套件名称
     */
    @ApiModelProperty(value = "基准测试套件名称")
    private String suite;

    @ApiModelProperty(value = "组件描述，用来存储客户的适用产品")
    private String suiteDescribe;
    /**
     * 该套件需要的参数个数
     */
    @ApiModelProperty(value = "基准测试套件参数")
    private String args;

    @ApiModelProperty(value = "基准测试套件前缀")
    private String pre;

    @ApiModelProperty(value = "基准测试套件pid")
    private int pid;

    @ApiModelProperty(value = "基准测试套件结果")
    private String result;
}
