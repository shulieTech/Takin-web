package io.shulie.takin.cloud.common.pojo.vo.middleware;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 中间件jar包 导入, 接收对象
 *
 * @author liuchuan
 * @date 2021/4/26 11:00 上午
 */
@Data
public class ImportMiddlewareJarVO {

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
    @Excel(name = "groupId", orderNum = "3")
    private String groupId;

    /**
     * artifactId 项目名称
     */
    @Excel(name = "artifactId", orderNum = "4")
    private String artifactId;

    /**
     * 版本号, 当 groupId, artifactId 存在时
     * 状态且为无需支持, version 可以不填写
     */
    @Excel(name = "版本号", orderNum = "5")
    private String version;

    @Excel(name = "状态", orderNum = "6")
    private String statusDesc;

}
