package io.shulie.takin.web.biz.pojo.response.leakcheck;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源sql配置详情返回的对象")
public class LeakSqlRefsResponse {

    private Long businessActivityId;

    private Long datasourceId;

    private List<String> sqls;
}
