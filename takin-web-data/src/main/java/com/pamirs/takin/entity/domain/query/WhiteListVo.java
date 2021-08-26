package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-04-20 19:05
 */

@Data
@ApiModel(value = "whiteListVo", description = "白名单配置入参")
public class WhiteListVo implements Serializable {

    private static final long serialVersionUID = -7311177264373439981L;

    /**
     * 白名单id
     */
    @ApiModelProperty(name = "id", value = "白名单id")
    private Long id;

    @ApiModelProperty(name = "list", value = "地址数组")
    private List<String> list;

    /**
     * 白名单类型
     */
    @ApiModelProperty(name = "type", value = "白名单类型")
    private Integer type;

    /**
     * 字典分类
     */
    @ApiModelProperty(name = "dictType", value = "字典类型")
    private String dictType;

    /**
     * 应用id
     */
    @ApiModelProperty(name = "applicationId", value = "应用id")
    private String applicationId;

    /**
     * 应用名称
     */
    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;

    /**
     * 是否可用
     */
    @ApiModelProperty(name = "useYn", value = "是否启用")
    private Integer useYn;

    @ApiModelProperty(name = "interfaceName", value = "接口名称")
    private String interfaceName;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private String updateTime;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private String createTime;

    @ApiModelProperty(name = "ids", value = "白名单id数组，批量删除使用")
    private List<Long> ids;

}
