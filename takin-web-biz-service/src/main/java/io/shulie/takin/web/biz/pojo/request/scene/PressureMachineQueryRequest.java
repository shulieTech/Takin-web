package io.shulie.takin.web.biz.pojo.request.scene;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "创建 压力机")
public class PressureMachineQueryRequest extends PagingDevice {
    private String name;
}
