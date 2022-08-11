package io.shulie.takin.web.biz.pojo.request.scene;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "Benchmark组件请求参数")
public class BenchmarkSuitePageRequest extends PagingDevice {

    private String name;

    private Integer pid;
}
