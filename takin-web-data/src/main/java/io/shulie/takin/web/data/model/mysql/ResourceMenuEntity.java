package io.shulie.takin.web.data.model.mysql;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/3 7:34 下午
 */
@Data
public class ResourceMenuEntity {

    private Long id;

    private Long parentId;

    private String code;

    private String name;

    private String value;

    private String sequence;

    private String action;
}
