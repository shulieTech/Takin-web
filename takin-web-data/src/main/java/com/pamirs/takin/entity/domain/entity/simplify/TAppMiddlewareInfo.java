/* https://github.com/orange1438 */
package com.pamirs.takin.entity.domain.entity.simplify;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.domain.entity.simplify
 * @date 2020-03-24 20:17
 */
@Data
public class TAppMiddlewareInfo {
    private Long id;

    private Long applicationId;

    private String jarName;

    private String pluginName;

    private String jarType;

    private Boolean active;

    private Boolean hidden;

    private Long userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date updateTime;

}
