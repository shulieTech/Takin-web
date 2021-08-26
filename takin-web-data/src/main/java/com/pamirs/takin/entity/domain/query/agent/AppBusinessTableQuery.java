package com.pamirs.takin.entity.domain.query.agent;

import com.pamirs.takin.entity.domain.query.AbstractQueryPage;
import lombok.Data;

@Data
public class AppBusinessTableQuery extends AbstractQueryPage {

    private Long id;

    private Long applicationId;

    private String tableName;

    private String url;

    private Long userId;
}
