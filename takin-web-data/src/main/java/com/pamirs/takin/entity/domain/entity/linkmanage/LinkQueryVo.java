package com.pamirs.takin.entity.domain.entity.linkmanage;

import lombok.Data;

/**
 * 系统流程查询封装
 *
 * @author vernon
 * @date 2019/12/9 01:22
 */
@Data
public class LinkQueryVo {
    //链路id
    private Long id;
    //业务链路名字
    private String name;
    //入口名字
    private String entrance;
    //是否变更
    private String isChange;
    //中间件类型
    private String middleWareType;
    //中间件名字
    private String middleWareName;
    //中间件版本
    private String middleWareVersion;
    //业务域
    private String domain;
    //系统流程名字
    private String systemProcessName;
}
