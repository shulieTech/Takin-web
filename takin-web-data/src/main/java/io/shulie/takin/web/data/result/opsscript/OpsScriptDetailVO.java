package io.shulie.takin.web.data.result.opsscript;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * (OpsScriptVO)
 *
 * @author caijy
 * @date 2021-05-18 16:48:12
 */
@Data
public class OpsScriptDetailVO {
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
    @ApiModelProperty(value = "文件")
    private List<OpsScriptFileVO> files;
    @ApiModelProperty(value = "附件")
    private List<OpsScriptFileVO> attachmentfiles;

}
