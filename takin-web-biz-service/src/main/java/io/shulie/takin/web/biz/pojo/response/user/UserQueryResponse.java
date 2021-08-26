package io.shulie.takin.web.biz.pojo.response.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ZhangXT
 * @date 2020/11/4 14:54
 */
@Data
public class UserQueryResponse {
    /**
     * 成员/账号id
     */
    @JsonProperty("id")
    private Long id;
    /**
     * 成员/账号名称
     */
    @JsonProperty("accountName")
    private String accountName;
    /**
     * 所在部门
     */
    @JsonProperty("department")
    private String department;
    /**
     * 账号角色
     */
    @JsonProperty("accountRole")
    private List<RoleQueryResponse> roleList;

    @JsonProperty("userAppKey")
    private String userAppKey;
}
