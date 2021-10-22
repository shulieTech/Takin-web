package io.shulie.takin.web.common.vo.application;

import java.util.List;

import io.shulie.takin.common.beans.component.SelectVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/29 12:35 上午
 */
@Data
public class  AppRemoteCallListVO extends AppRemoteCallVO {
    @ApiModelProperty(name = "id", value = "数据库id")
    private Long id;
    /**
     * 服务端应用名列表
     */
    @ApiModelProperty(name = "serverAppNames", value = "前端显示服务端应用名列表")
    private List<String> serverAppNames;

    /**
     * 前端显示接口类型
     */
    @ApiModelProperty(name = "interfaceTypeSelectVO", value = "前端显示接口类型")
    private SelectVO interfaceTypeSelectVO;

    /**
     * 前端显示配置类型
     */
    @ApiModelProperty(name = "typeSelectVO", value = "前端显示配置类型")
    private SelectVO typeSelectVO;

    /**
     * 加入白名单情况，无服务端应用情况
     */
    @ApiModelProperty(name = "isException", value = "是否有异常")
    private Boolean isException;

    /**
     * 服务端应用名个数
     */
    @ApiModelProperty(name = "count", value = "服务端应用名个数")
    private Integer count;

    @ApiModelProperty(name = "canEdit", value = "编辑权限")
    private Boolean canEdit = true;

    @ApiModelProperty(name = "canRemove", value = "删除权限")
    private Boolean canRemove = true;

    @ApiModelProperty(name = "canEnableDisable", value = "启动关闭权限")
    private Boolean canEnableDisable = true;

    /**
     * 排序用
     */
    private Integer sort;


    /**
     * 默认是白名单时显示原因
     */
    @ApiModelProperty(name = "defaultWhiteInfo", value = "默认白名单信息")
    private String defaultWhiteInfo;


    @ApiModelProperty(name = "mockValue", value = "mock内容")
    private String mockValue;
}
