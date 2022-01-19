package io.shulie.takin.web.biz.pojo.response;

import io.shulie.takin.web.biz.pojo.vo.AnnualReportContentVO;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 第三方登录服务表(AnnualReport)controller 详情响应类
 *
 * @author liuchuan
 * @date 2021-12-30 10:54:24
 */
@ApiModel("出参类-详情出参")
@Data
public class AnnualReportDetailResponse {

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("租户logo")
    private String tenantLogo;

    @ApiModelProperty("年度数据对象")
    private AnnualReportContentVO content;

}
