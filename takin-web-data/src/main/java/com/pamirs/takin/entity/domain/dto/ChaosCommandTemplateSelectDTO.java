package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;

/**
 * @author 710524
 * @ClassName: ChaosCommandTemplateSelectDTO
 * @package: com.pamirs.takin.entity.domain.dto
 * @date 2019年 05月15日 15:38
 */
public class ChaosCommandTemplateSelectDTO implements Serializable {

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
