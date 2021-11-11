package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Data
@ApiModel
public class ReportTrendQueryParam implements Serializable {

    private static final long serialVersionUID = -8391664190402372494L;

    /**
     * 报表ID
     */
    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    /**
     * 场景ID
     */
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    /**
     * 节点MD5值
     */
    @ApiModelProperty(value = "节点MD5值")
    private String xpathMd5;

}
