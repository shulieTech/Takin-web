package io.shulie.takin.web.common.domain;

import java.io.Serializable;

import lombok.Data;
import org.springframework.http.HttpMethod;

/**
 * @author hezhongqi
 * @Package io.shulie.takin.web.common.domain
 * @ClassName: WebRequest
 * @description:老版请求方式
 * @date 2021/8/10 15:54
 */
@Data
@Deprecated
public class WebRequest /*extends CloudUserCommonRequestExt*/ implements Serializable {

    private static final long serialVersionUID = -1529428936481160409L;

    private transient String requestUrl;

    private transient HttpMethod httpMethod;

}
