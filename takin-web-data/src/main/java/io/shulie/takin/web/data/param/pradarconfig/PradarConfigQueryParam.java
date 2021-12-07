package io.shulie.takin.web.data.param.pradarconfig;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author junshao
 * @date 2021/07/08 2:35 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PradarConfigQueryParam extends PageBaseDTO {

    private Long id;

    private String startTime;

    private String endTime;

    private String zkPath;

    private String type;

    private String value;

    private String remark;
}
