package io.shulie.takin.web.biz.pojo.request.fastagentaccess;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description agent版本查询对象
 * @Author ocean_wll
 * @Date 2021/8/12 3:39 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("入参-agent版本列表查询类")
public class AgentVersionQueryRequest extends PageBaseDTO {

    private static final long serialVersionUID = 7908819806350087243L;

    @ApiModelProperty(value = "版本号")
    private String version;
    //
    //@ApiModelProperty(value = "大版本号")
    //private String firstVersion;
}
