/* https://github.com/orange1438 */
package com.pamirs.takin.entity.domain.entity.simplify;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class AppBusinessTableInfo {
    private Long id;

    private Long applicationId;

    private String tableName;

    private String url;

    private Long userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date updateTime;
}
