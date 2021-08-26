package io.shulie.takin.web.biz.pojo.response.application;

import io.shulie.takin.web.data.model.mysql.MiddlewareJarEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class MiddlewareJarResponse extends MiddlewareJarEntity {

    private boolean canEdit;

}
