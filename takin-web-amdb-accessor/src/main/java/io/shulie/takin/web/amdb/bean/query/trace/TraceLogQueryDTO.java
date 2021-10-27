package io.shulie.takin.web.amdb.bean.query.trace;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.amdb.bean.query.trace
 * @ClassName: TraceLogQueryDTO
 * @Description: TODO
 * @Date: 2021/10/25 17:30
 */
@Data
public class TraceLogQueryDTO  {

    private Long startTime;

    private Long endTime;

    @ApiModelProperty("应用名")
    private String applicationName;

    @ApiModelProperty("接口名")
    private String interfaceName;


    private Integer pageNum;

    private Integer pageSize;


}
