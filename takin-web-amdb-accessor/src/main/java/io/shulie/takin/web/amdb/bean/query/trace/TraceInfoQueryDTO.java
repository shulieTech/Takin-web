package io.shulie.takin.web.amdb.bean.query.trace;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-10-12
 */
@Data
public class TraceInfoQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询数据类型(1-agent上报trace明细,2-压测报告请求trace明细)
     * 默认查询trace明细
     */
    private Integer queryType;

    private Long startTime;

    private Long endTime;

    private String type;

    private List<String> entranceList;

    /**
     * 入口
     */
    private List<EntranceRuleDTO> entranceRuleDTOS;


    private Integer pageNum;

    private Integer pageSize;

    /**
     * 报告id
     */
    private Long reportId;

}
