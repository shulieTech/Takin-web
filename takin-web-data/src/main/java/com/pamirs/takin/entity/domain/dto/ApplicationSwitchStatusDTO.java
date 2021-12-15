package com.pamirs.takin.entity.domain.dto;

import java.util.Map;
import java.util.List;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-04-07 13:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationSwitchStatusDTO extends AuthQueryResponseCommonExt implements Serializable {
    private static final long serialVersionUID = -8743442521630586570L;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * OPENED("已开启",0),
     * OPENING("开启中",1),
     * OPEN_FAILING("开启异常",2),
     * CLOSED("已关闭",3),
     * CLOSING("关闭中",4),
     * CLOSE_FAILING("关闭异常",5)
     */
    private String switchStatus;

    /**
     * 异常信息
     */
    private Map<String, Object> exceptionMap;

    /**
     * 节点唯一键
     */
    private String nodeKey;

    /**
     * 节点ip
     */
    private String nodeIP;

    /**
     * 节点列表信息
     */
    private List<ApplicationVo> errorList;

    /**
     * 应用接入状态
     */
    private Integer accessStatus;

    /**
     * OPENED("已开启",0),
     * OPENING("开启中",1),
     * OPEN_FAILING("开启异常",2),
     * CLOSED("已关闭",3),
     * CLOSING("关闭中",4),
     * CLOSE_FAILING("关闭异常",5)
     */
    private String silenceSwitchOn;

}
