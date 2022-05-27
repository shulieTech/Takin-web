package io.shulie.takin.web.common.domain;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/11 下午2:24
 */
@Data
@Deprecated
public class PradarWebRequest extends WebRequest {

    private static final long serialVersionUID = -1529428936481160409L;

    private String traceId;
}
