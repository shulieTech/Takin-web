package io.shulie.takin.adapter.api.model.request.common;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公共信息请求
 *
 * @author lipeng
 * @date 2021-06-24 4:24 下午
 */
@Data
@ApiModel("公共信息请求入参")
@EqualsAndHashCode(callSuper = true)
public class CloudCommonInfoWrapperReq extends ContextExt {
    //暂时无入参
}
