package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.util.List;

import com.pamirs.takin.entity.domain.risk.ReportLinkDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 报告链路明细
 *
 * @author qianshui
 * @date 2020/8/21 上午11:02
 */
@Data
@ApiModel
public class ReportPradarLinkDTO implements Serializable {

    private static final long serialVersionUID = 3770206572844505462L;

    @ApiModelProperty(value = "总时长")
    private Integer totalRT;

    private List<ReportLinkDetail> details;
}
