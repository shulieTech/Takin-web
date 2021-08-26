package com.pamirs.takin.entity.domain.dto.config;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author mubai
 * @date 2021-03-01 20:25
 */

@Data
public class ImportConfigDTO implements Serializable {
    private static final long serialVersionUID = 4216961120177283169L;
    /**
     *
     */
    private Long data;

    /**
     *
     */
    private List<String> msg;

}
