package io.shulie.takin.web.biz.pojo.dto.scene;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EnginePressureQuery {
    /**
     * 特殊查询条件，传入了使用特殊的查询条件
     */
    private Map<String, String> fieldAndAlias;

    private Long startTime;

    private Long endTime;

    private String transaction;

    private Long jobId;

    /**
     * 租户标识
     */
    private String tenantAppKey;
    /**
     * 环境标识
     */
    private String envCode;

    private Integer limit;

    @ApiModelProperty("排序策略,0:升序;1:降序")
    private Integer orderByStrategy;

    /**
     * groupBy字段
     */
    private List<String> groupByFields;
}
