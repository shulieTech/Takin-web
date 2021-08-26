package io.shulie.takin.web.data.result.leakverify;

import java.util.Date;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/6 4:20 下午
 */
@Data
public class LeakVerifyResultResult {
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

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否有效 0:有效;1:无效
     */
    private Boolean isDeleted;
}
