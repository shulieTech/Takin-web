package io.shulie.takin.cloud.ext.content.enginecall;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:06
 */
@Data
@ApiModel(description = "分配策略列表")
public class StrategyConfigExt {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "配置名称")
    private String strategyName;

    @ApiModelProperty(value = "并发数")
    private Integer threadNum;

    @ApiModelProperty(value = "tps数")
    private Integer tpsNum;

    @ApiModelProperty(value = "cpu")
    private BigDecimal cpuNum;

    @ApiModelProperty(value = "内存")
    private BigDecimal memorySize;

    /**
     * 添加限制cpu和限制内存
     * add by 李鹏 2021/06/23
     */
    @ApiModelProperty(value = "限制cpu")
    private BigDecimal limitCpuNum;

    @ApiModelProperty(value = "限制内存")
    private BigDecimal limitMemorySize;

    @ApiModelProperty(value = "最后修改时间")
    private String updateTime;

    @ApiModelProperty(value = "发布方式")
    private String deploymentMethod;

    @ApiModelProperty(value = "tps模式：0新的模式（ConcurrencyThreadGroup+常量吞吐量定时器），1老的模式（ArrivalsThreadGroup）")
    private Integer tpsThreadMode;

    @ApiModelProperty(value = "TPS常量吞吐量模式目标tps上浮比例，默认0.1表示上浮10%")
    private double tpsTargetLevelFactor = 0.1;

    @ApiModelProperty(value = "tps模式下真实线程数，tps模式为0时才生效，null或者0表示自行按基准2c3G1500个并发线程比例计算")
    private Integer tpsRealThreadNum;

    @ApiModelProperty(value = "压测引擎版本")
    private String pressureEngineImage;

    @ApiModelProperty(value = "压测引擎名称")
    private String pressureEngineName;

    @ApiModelProperty(value = "单个pod的JVM信息配置")
    private String k8sJvmSettings;

    @ApiModelProperty(value = "文件分隔符")
    private String delimiter;
}
