package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.util.Collections;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 10:53 上午
 */
@Data
@ApiModel("出参类-脚本调试出参")
public class ScriptDebugResponse {

    @ApiModelProperty("脚本调试id")
    private Long scriptDebugId;

    @ApiModelProperty("错误信息数组")
    private List<String> errorMessages = Collections.emptyList();

}
