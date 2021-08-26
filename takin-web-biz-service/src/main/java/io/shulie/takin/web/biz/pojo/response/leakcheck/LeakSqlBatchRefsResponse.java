package io.shulie.takin.web.biz.pojo.response.leakcheck;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/4 3:58 下午
 */
@Data
@ApiModel("业务活动关联的sql配置")
public class LeakSqlBatchRefsResponse {

    //private Long businessActivityId;

    private Long datasourceId;

    private String datasourceName;

    private String jdbcUrl;

    private List<SingleSqlDetailResponse> sqlResponseList;
}
