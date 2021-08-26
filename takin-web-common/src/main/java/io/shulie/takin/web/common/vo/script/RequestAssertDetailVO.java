package io.shulie.takin.web.common.vo.script;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 请求流量明细, 断言结果
 *
 * @author liuchuan
 * @date 2021/5/13 9:22 下午
 */
@Data
@ApiModel("出参类 --> 断言详情出参类")
public class RequestAssertDetailVO {

    @ApiModelProperty("断言名称")
    private String assertName;

    @ApiModelProperty("断言信息")
    private String assertMessage;

}
