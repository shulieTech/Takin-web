package io.shulie.takin.web.biz.pojo.response.leakverify;

import io.shulie.takin.common.beans.component.SelectVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/6 5:42 下午
 */
@Data
public class LeakVerifyDetailResponse {

    @ApiModelProperty("序号")
    private Integer order;

    @ApiModelProperty("命令")
    private String sql;

    @ApiModelProperty("当前sql的验证结果")
    private Integer status;

    @ApiModelProperty("验证结果对象")
    private SelectVO statusResponse;

    @ApiModelProperty("告警级别")
    private Integer warningLevel;
}
