package io.shulie.takin.web.ext.entity.e2e;

import lombok.Data;

/**
 * 巡检异常配置
 *
 * @author 张天赐
 */
@Data
public class E2eExceptionConfigInfoExt {
    /**
     * 主键
     */
    private Long id;
    /**
     * 进行判断时的顺序
     */
    private Integer orderNumber;
    /**
     * 异常类型
     */
    private Integer typeValue;
    /**
     * 异常等级
     */
    private Integer levelValue;
    /**
     * 阈值
     */
    private Double thresholdValue;
    /**
     * 对比因子
     */
    private Integer contrastFactor;
    /**
     * 备注
     */
    private String remarks;
}
