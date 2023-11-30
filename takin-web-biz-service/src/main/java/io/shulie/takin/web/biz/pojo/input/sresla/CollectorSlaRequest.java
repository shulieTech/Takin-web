package io.shulie.takin.web.biz.pojo.input.sresla;

import lombok.Data;

import java.util.Date;

/**
 * @author zhaoyong
 */
@Data
public class CollectorSlaRequest {

    private Date startDate;

    private Date endDate;

    private String appName;

    private String rpc;

}
