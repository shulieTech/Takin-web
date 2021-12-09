package io.shulie.takin.web.biz.pojo.response.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.biz.pojo.response.dashboard
 * @ClassName: ApplicationStatusResponse
 * @Description: TODO
 * @Date: 2021/12/3 14:25
 */
@Data
public class ApplicationStatusResponse {
    /**
     * 应用接入数量
     */
    @ApiModelProperty(value = "应用数量")
    private Long applicationNum;
    /**
     * 接入异常数量
     */
    @ApiModelProperty(value = "应用接入异常数量")
    private Long accessErrorNum;
}
