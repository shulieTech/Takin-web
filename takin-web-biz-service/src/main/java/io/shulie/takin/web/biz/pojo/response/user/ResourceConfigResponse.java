package io.shulie.takin.web.biz.pojo.response.user;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/2 8:52 下午
 */
@Data
public class ResourceConfigResponse {

    private Long id;

    private String code;

    private String name;

    private List<String> action;
}
