package io.shulie.takin.web.biz.pojo.dto.application;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liuchuan
 * @date 2021/7/7 10:50 上午
 */
@Getter
@Setter
@ToString
public class CompareApplicationMiddlewareDTO {

    private Long id;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 项目名称
     */
    private String artifactId;

    /**
     * 项目组织名称
     */
    private String groupId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 类型, 字符串形式
     */
    private String type;

    /**
     * 状态, 1已支持, 2 未支持, 3 无需支持, 4 未知, 0 无
     */
    private Integer status;

}
