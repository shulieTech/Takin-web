package io.shulie.takin.web.biz.pojo.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/13 4:44 下午
 */
@Data
public class UserLoginResponse {
    private Long id;

    private String name;

    private String key;

    private Integer userType;

    @JsonProperty("xToken")
    private String xToken;

    private Boolean expire;

}
