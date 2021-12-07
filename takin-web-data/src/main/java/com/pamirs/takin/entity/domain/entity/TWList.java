package com.pamirs.takin.entity.domain.entity;

import java.util.Date;

import lombok.Data;

/**
 * 说明：白名单实体类
 *
 * @author shulie
 * @version V1.0
 * @date 2018年3月1日 下午12:49:55
 */
@Data
public class TWList extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 白名单id
     */
    private long wlistId;

    /**
     * 接口名称
     */
    //    @NotBlank(message = "接口名称不能为空")
    private String interfaceName;

    /**
     * 白名单类型
     */
    private String type;

    /**
     * 字典分类
     */
    private String dictType;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 负责人工号
     */
    private String principalNo;

    /**
     * 是否可用
     */
    private String useYn;

    /**
     * mq类型： 1ESB 2IBM 3ROCKETMQ 4DPBOOT_ROCKETMQ
     * 当且仅当白名单类型是MQ(即type=5)时才有值
     */
    private String mqType;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * IP端口号, 即nameServer
     * 当且仅当mq是ROCKETMQ或DPBOOT_ROCKETMQ时有值
     */
    private String ipPort;

    /**
     * http类型：1页面 2接口
     */
    private String httpType;

    /**
     * 页面分类：1普通页面加载(3s) 2简单查询页面/复杂界面(5s) 3复杂查询页面(8s)
     */
    private String pageLevel;

    /**
     * 接口类型：1简单操作/查询 2一般操作/查询 3复杂操作 4涉及级联嵌套调用多服务操作 5调用外网操作
     */
    private String interfaceLevel;

    /**
     * JOB调度间隔：1调度间隔≤1分钟 2调度间隔≤5分钟 3调度间隔≤15分钟 4调度间隔≤60分钟
     */
    private String jobInterval;

    /**
     * 全局
     */
    private Boolean isGlobal;


    /**
     * 手工添加
     */
    private Boolean isHandwork;

    private Date gmtCreate;

    private Date gmtModified;



    public TWList() {
        super();
    }

    public static TWList build(String applicationId, String interfaceType, String interfaceName, String useYn,
                               String dictType) {
        TWList tWhiteList = new TWList();
        tWhiteList.setApplicationId(applicationId);
        tWhiteList.setType(interfaceType);
        tWhiteList.setInterfaceName(interfaceName);
        tWhiteList.setUseYn(useYn);
        tWhiteList.setDictType(dictType);
        return tWhiteList;
    }

    /**
     * 2018年5月17日
     *
     * @return 实体类字符串
     * @author shulie
     * @version 1.0
     */
    @Override
    public String toString() {
        return "TWList{" +
            "wlistId=" + wlistId +
            ", interfaceName='" + interfaceName + '\'' +
            ", type='" + type + '\'' +
            ", dictType='" + dictType + '\'' +
            ", applicationId='" + applicationId + '\'' +
            ", principalNo='" + principalNo + '\'' +
            ", useYn='" + useYn + '\'' +
            ", mqType='" + mqType + '\'' +
            ", queueName='" + queueName + '\'' +
            ", ipPort='" + ipPort + '\'' +
            ", httpType='" + httpType + '\'' +
            ", pageLevel='" + pageLevel + '\'' +
            ", interfaceLevel='" + interfaceLevel + '\'' +
            ", jobInterval='" + jobInterval + '\'' +
            ", isGlobal='" + isGlobal + '\'' +
            '}';
    }
}
