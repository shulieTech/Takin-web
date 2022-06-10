package com.pamirs.takin.entity.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "AppOperateAgentCheckVO", description = "操作检查入参")
public class AppOperateAgentCheckVO {

    private List<String> appIds;

    /**
     * uninstall 卸载操作
     * resume  恢复操作
     */
    private String operate;
}
