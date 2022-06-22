package com.pamirs.takin.cloud.entity.domain.bo.scenemanage;

import java.util.Date;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-18
 */
@Data
public class WarnBO {

    /**
     * 报告ID
     */
    private Long reportId;

    /**
     * SLA ID
     */
    private Long slaId;

    /**
     * SLA名称
     */
    private String slaName;

    /**
     * 活动ID
     */
    private Long businessActivityId;

    /**
     * 活动名称
     */
    private String businessActivityName;

    /**
     * 总数
     */
    private Long total;

    /**
     * 报警内容
     */
    private String content;

    /**
     * 最后更新时间
     */
    private Date lastWarnTime;

}
