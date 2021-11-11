package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/12 下午3:47
 */
@Data
public class ReportIdVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = -481550164375737963L;

    private Long reportId;

    private Long sceneId;
}
