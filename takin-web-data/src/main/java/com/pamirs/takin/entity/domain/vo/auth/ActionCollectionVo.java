package com.pamirs.takin.entity.domain.vo.auth;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/9/7 5:05 PM
 */
@Data
public class ActionCollectionVo {
    private Boolean enableAuth;
    private String actionStr;
    private List<String> actionList;
}
