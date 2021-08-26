package com.pamirs.takin.entity.domain.query.agent;

import com.pamirs.takin.entity.domain.query.AbstractQueryPage;
import lombok.Data;

@Data
public class AppMiddlewareQuery extends AbstractQueryPage {

    private Long id;

    private Long applicationId;

    private String jarName;

    private String pluginName;

    private String jarType;

    private Boolean active;

    private Long userId;

}
