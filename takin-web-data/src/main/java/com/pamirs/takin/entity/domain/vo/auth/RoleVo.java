package com.pamirs.takin.entity.domain.vo.auth;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/9/3 下午8:37
 */
@Data
public class RoleVo {
    List<ActionVo> actionList;
    private Long id;
    private Long applicationId;
    private String name;
    private String alias;
    private String code;
    private String description;
    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
    private String features;
}
