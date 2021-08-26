package io.shulie.takin.web.ext.entity;

import java.util.Date;

import lombok.Data;

/**
 * @author: 肖建璋
 * @date 2021/08/03/2:41 下午
 * @desc: 操作日志实体
 */
@Data
public class OperationLogExt {
    /**
     * 操作模块-主模块
     */
    private String module;

    /**
     * 操作模块-子模块
     */
    private String subModule;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作状态
     */
    private String status;

    /**
     * 操作内容描述
     */
    private String content;

    /**
     * 操作人id
     */
    private Long userId;

    /**
     * 操作人名称
     */
    private String userName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
}
