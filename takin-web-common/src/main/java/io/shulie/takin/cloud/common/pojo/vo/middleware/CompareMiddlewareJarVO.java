package io.shulie.takin.cloud.common.pojo.vo.middleware;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 中间件jar包 比对, 接收对象
 *
 * @author liuchuan
 * @date 2021/4/26 11:00 上午
 */
@Data
public class CompareMiddlewareJarVO {

    /**
     * groupId 组织id
     */
    @Excel(name = "GroupId", orderNum = "1")
    private String groupId;

    /**
     * artifactId 项目名称
     */
    @Excel(name = "ArtifactId", orderNum = "2")
    private String artifactId;

    @Excel(name = "版本", orderNum = "3")
    private String version;

}
