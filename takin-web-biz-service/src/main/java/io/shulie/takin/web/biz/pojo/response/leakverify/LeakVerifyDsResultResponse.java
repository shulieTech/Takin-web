package io.shulie.takin.web.biz.pojo.response.leakverify;

import java.util.List;

import io.shulie.takin.common.beans.component.SelectVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/6 5:43 下午
 */
@Data
public class LeakVerifyDsResultResponse {

    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("数据源地址")
    private String jdbcUrl;

    @ApiModelProperty("验证结果")
    private Integer status;

    @ApiModelProperty("告警级别")
    private Integer warningLevel;

    @ApiModelProperty("当前数据源的漏数汇总结果")
    private SelectVO statusResponse;

    @ApiModelProperty("sql验证详情")
    private List<LeakVerifyDetailResponse> detailResponseList;
}
