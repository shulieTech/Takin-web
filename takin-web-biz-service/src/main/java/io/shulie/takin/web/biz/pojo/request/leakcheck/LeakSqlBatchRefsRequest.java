package io.shulie.takin.web.biz.pojo.request.leakcheck;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/4 4:11 下午
 */
@Data
public class LeakSqlBatchRefsRequest {

    @ApiModelProperty("业务活动id")
    @NotEmpty
    private List<Long> businessActivityIds;
}
