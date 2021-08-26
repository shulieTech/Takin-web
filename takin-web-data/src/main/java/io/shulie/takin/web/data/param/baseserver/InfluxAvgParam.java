package io.shulie.takin.web.data.param.baseserver;

import lombok.Data;

/**
 * 查询平均值
 * @author mubai
 * @date 2020-10-26 16:29
 */

@Data
public class InfluxAvgParam {

    private Long sTime ;

    private Long eTime;

    private String appName ;

    private String event ;


}
