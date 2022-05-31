package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 10:45 上午
 */
@Data
public class PerformanceParamRequest implements Serializable {
    /**
     * 列索引
     */
    private Integer fileColumnIndex;

    /**
     * 数据来源
     */
    private String paramValue;

    /**
     * 参数类型 1:自定义参数,2:数据源参数
     */
    private Integer type;

    /**
     * 参数名
     */
    private String paramName;

    /**
     * 文件Id
     */
    private Long fileId;

    private Long configId;

    private String filePath;
}
