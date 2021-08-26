package io.shulie.takin.web.biz.pojo.input.whitelist;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/16 3:05 下午
 */
@Data
@ApiModel(value = "WhitelistImportFromExcelInput", description = "白名单导入功能")
public class WhitelistImportFromExcelInput {

    @ApiModelProperty(name = "applicationId", value = "应用ID")
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @ApiModelProperty(name = "interfaceType", value = "接口类型")
    @NotNull(message = "接口类型不能为空")
    private Integer interfaceType;

    @ApiModelProperty(name = "interfaceName", value = "接口列表")
    @NotNull(message = "接口类型不能为空")
    private String interfaceName;

    @ApiModelProperty(name = "dictType", value = "数据字典")
    @NotNull(message = "数据类型")
    private String dictType;

    /**
     * 状态
     */
    @ApiModelProperty(name = "useYn", value = "状态")
    @NotNull(message = "状态")
    private Integer useYn;

    /**
     * 全局
     */
    @ApiModelProperty(name = "isGlobal", value = "全局")
    @NotNull(message = "全局")
    private Boolean isGlobal;

    /**
     * 手工
     */
    @ApiModelProperty(name = "isHandwork", value = "手工")
    @NotNull(message = "手工")
    private Boolean isHandwork;

    /**
     * 生效应用
     */
    @ApiModelProperty(name = "effectAppNames", value = "生效应用")
    @NotNull(message = "生效应用")
    private List<String> effectAppNames;

}
