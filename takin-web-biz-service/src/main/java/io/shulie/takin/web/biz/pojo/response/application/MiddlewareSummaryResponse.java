package io.shulie.takin.web.biz.pojo.response.application;

import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;
import lombok.Data;

@Data
public class MiddlewareSummaryResponse extends MiddlewareSummaryEntity {
    private Boolean canEdit = Boolean.FALSE;
}
