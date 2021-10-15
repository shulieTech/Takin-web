package io.shulie.takin.web.amdb.bean.query.fastagentaccess;

import java.util.Date;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 异常日志查询对象
 * @Author ocean_wll
 * @Date 2021/8/18 4:05 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorLogQueryDTO extends PageBaseDTO {

    private static final long serialVersionUID = -6512502356697415777L;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用名称集合，多个应用以,分割
     */
    private String appNames;

    /**
     * agentId
     */
    private String agentId;

    /**
     * tenantAppKey
     */
    private String tenantAppKey;

    /**
     * 环境编码
     */
    private String envCode;

    /**
     * 关键词
     */
    private String agentInfo;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;
}
