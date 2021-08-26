package io.shulie.takin.web.data.param.leakverify;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 8:16 下午
 */
@Data
public class LeakVerifyResultCreateParam {
    private Long id;

    /**
     * 引用类型 0:压测场景;1:业务流程;2:业务活动
     */
    private Integer refType;

    /**
     * 引用id
     */
    private Long refId;

    /**
     * 报告id
     */
    private Long reportId;

    /**
     * 数据源id
     */
    private Long dbresourceId;

    /**
     * 数据源名称
     */
    private String dbresourceName;

    /**
     * 数据源地址
     */
    private String dbresourceUrl;

    /**
     * 是否漏数 0:正常;1:漏数;2:未检测;3:检测失败
     */
//    private Integer status;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;
}
