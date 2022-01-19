package io.shulie.takin.web.data.result.linkmange;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/16 5:40 下午
 */
@Data
public class BusinessLinkResult extends UserCommonExt {

    private Long linkId;
    private String id;
    private String linkName;
    // 取名有问题
    private String entrace;
    private String ischange;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private TechLinkResult techLinkResult;
    private String candelete;
    private String isCore;
    private String linkLevel;
    private String businessDomain;
    private BusinessLinkResult child;

    private String relatedTechLink;

    /**
     * 类型：0：正常业务活动 1：虚拟业务活动
     */
    private Integer type;
    /**
     * 绑定业务活动id
     */
    private Long bindBusinessId;

    /**
     * kafka,rabbitmq,http.....
     */
    private String serverMiddlewareType;

    /**
     * 应用名
     */
    private String applicationName;
}
