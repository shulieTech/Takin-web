package io.shulie.takin.web.common.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.http.HttpMethod;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * 老版请求方式
 *
 * @date 2021/8/10 15:54
 */
@Data
@Deprecated
@EqualsAndHashCode(callSuper = true)
public class WebRequest extends ContextExt implements Serializable {

    private static final long serialVersionUID = -1529428936481160409L;

    private transient String requestUrl;

    private transient HttpMethod httpMethod;

}
