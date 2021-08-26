package com.pamirs.takin.entity.domain.vo.middleware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author liqiyu
 */
@Data
public class ImportExcelFixVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 中间件类型
     */
    @Excel(name = "中间件类型", orderNum = "1")
    private String type;

    /**
     * 中间件名称
     */
    @Excel(name = "中间件名称", orderNum = "2")
    private String name;

    /**
     * groupId 组织id
     */
    @Excel(name = "GroupId", orderNum = "3")
    private String groupId;

    /**
     * artifactId 项目名称
     */
    @Excel(name = "ArtifactId", orderNum = "4")
    private String artifactId;

    /**
     * 版本号, 当 groupId, artifactId 存在时
     * 状态且为无需支持, version 可以不填写
     */
    @Excel(name = "版本", orderNum = "5")
    private String version;

    @Excel(name = "状态", orderNum = "6")
    private String statusDesc;

    @Excel(name = "比对备注", orderNum = "7")
    private String remark;

    private Long appCompareId;

    private List<String> remarkList = new ArrayList<>(0);

    public void addRemark(String remark){
        remarkList.add(remark);
    }
}
