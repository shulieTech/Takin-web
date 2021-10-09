package io.shulie.takin.web.amdb.bean.query.application;

import java.util.Date;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ApplicationNodeQueryDTO extends PageBaseDTO {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * ip
     */
    private String ip;

    /**
     * 应用名称, 复数
     */
    private String appNames;

    /**
     * 应用节点IP地址
     */
    private String ipAddress;

    /**
     * 客户Id
     */
    private String customerId;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 探针状态, 0-已安装,1-未安装,2-安装中,3-卸载中,4-安装失败,99-未知状态
     */
    private Integer probeStatus;

    /**
     * agent状态, 0-已安装,1-未安装,2-安装中,3-卸载中,4-安装失败,99-未知状态
     */
    private Integer agentStatus;

    /**
     * 查询条件，更新时间下限
     */
    private Date minUpdateDate;

}
