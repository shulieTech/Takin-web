package com.pamirs.takin.entity.domain.vo.middleware;

import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MiddlewareSummaryExportVO {
    @ApiModelProperty("ID")
    @Excel(name = "ID")
    private Long id;


    /**
     * 中间件类型
     */
    @ApiModelProperty("中间件类型")
    @Excel(name = "中间件类型")
    private String type;

    /**
     * 中间件中文名称
     */
    @ApiModelProperty("中间件中文名称")
    @Excel(name = "中间件名称")
    private String name;


    /**
     * groupId
     */
    @ApiModelProperty("groupId")
    @Excel(name = "groupId")
    private String groupId;

    /**
     * artifactId
     */
    @ApiModelProperty("artifactId")
    @Excel(name = "artifactId")
    private String artifactId;


    @ApiModelProperty("状态, 3 已支持, 2 未支持, 4 无需支持, 1 未知")
    @Excel(name = "中间件支持情况")
    private String statusDesc;

    /**
     * 中间件总数
     */
    @ApiModelProperty("中间件总数")
    @Excel(name = "总版本")
    private Long totalNum;

    /**
     * 已支持数量
     */
    @ApiModelProperty("已支持数量")
    @Excel(name = "已支持版本")
    private Long supportedNum;

    /**
     * 未知数量
     */
    @ApiModelProperty("未知数量")
    @Excel(name = "未知")
    private Long unknownNum;

    /**
     * 无需支持的数量
     */
    @ApiModelProperty("无需支持")
    @Excel(name = "无需支持")
    private Long notSupportedNum;


    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间",exportFormat = "yyyy年MM月dd日 HH:mm:ss")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间",exportFormat = "yyyy年MM月dd日 HH:mm:ss")
    private Date gmtUpdate;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @Excel(name = "备注")
    private String commit;

}
