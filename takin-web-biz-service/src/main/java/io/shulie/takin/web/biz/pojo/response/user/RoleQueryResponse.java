package io.shulie.takin.web.biz.pojo.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/2 4:16 下午
 */
@Data
public class RoleQueryResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("roleName")
    private String name;

    @JsonProperty("roleDesc")
    private String description;
}
