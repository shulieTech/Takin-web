package com.pamirs.takin.entity.domain.vo;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "AppUninstallAgentVO", description = "一键卸载探针入参")
public class AppUninstallAgentVO {

    @NotEmpty(message = "应用id列表" + AppConstants.MUST_BE_NOT_NULL)
    private List<String> appIds;

}
