package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Data
@ApiModel("应用入口服务查询对象")
public class ApplicationEntrancesQueryRequest {

    @NotEmpty
    @ApiModelProperty("应用名称")
    private String applicationName;

    @NotNull
    @ApiModelProperty("入口服务类型")
    private EntranceTypeEnum type;

}
