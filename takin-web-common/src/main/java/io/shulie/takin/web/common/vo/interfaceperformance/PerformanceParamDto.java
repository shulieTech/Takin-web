package io.shulie.takin.web.common.vo.interfaceperformance;

import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 3:05 下午
 */
@Data
public class PerformanceParamDto {
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口压测配置Id
     */
    private Long configId;

    /**
     * 参数名
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 类型 1:自定义参数,2:数据源参数
     */
    private Integer type;

    /**
     * 列索引
     */
    private Integer fileColumnIndex;

    /**
     * 文件Id
     */
    private Long fileId;
}
