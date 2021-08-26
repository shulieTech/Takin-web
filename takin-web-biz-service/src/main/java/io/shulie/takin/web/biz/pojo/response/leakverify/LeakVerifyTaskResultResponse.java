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
public class LeakVerifyTaskResultResponse {

    @ApiModelProperty("数据源验证结果")
    List<LeakVerifyDsResultResponse> dsResultResponseList;
    @ApiModelProperty("实体类型")
    private Integer refType;
    @ApiModelProperty("压测场景id")
    private Long refId;
    @ApiModelProperty("当前压测场景的漏数汇总结果")
    private Integer status;
    @ApiModelProperty("验证结果对象")
    private SelectVO statusResponse;
}
