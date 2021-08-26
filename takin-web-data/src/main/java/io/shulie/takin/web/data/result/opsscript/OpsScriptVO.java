package io.shulie.takin.web.data.result.opsscript;

import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * (OpsScriptVO)
 *
 * @author caijy
 * @date 2021-05-18 16:48:12
 */
@Data
public class OpsScriptVO extends AuthQueryResponseCommonExt {
    private String id;

    /**
     * 脚本名称
     */
    @ApiModelProperty(value = "脚本名称")
    private String name;

    /**
     * 来源于字典。脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本
     */
    @ApiModelProperty(value = "脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本")
    private Integer scriptType;

    @ApiModelProperty(value = "脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本")
    private String sciptTypeName;

    /**
     * 执行状态 0=待执行,1=执行中,2=已执行
     */
    @ApiModelProperty(value = "执行状态 0=待执行,1=执行中,2=已执行")
    private Integer status;
    @ApiModelProperty(value = "执行状态 0=待执行,1=执行中,2=已执行")
    private String statusName;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private String lastModefyTime;

    /**
     * 最后执行时间
     */
    @ApiModelProperty(value = "最后执行时间")
    private String lastExecuteTime;
}
