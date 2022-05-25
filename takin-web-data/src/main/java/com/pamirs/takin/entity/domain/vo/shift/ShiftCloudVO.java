package com.pamirs.takin.entity.domain.vo.shift;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "移动云接口入参")
public class ShiftCloudVO extends BaseShiftCloudVO {
    private String account;
    private String project_id;
    private int page_index = 1;
    private int page_size = 10;
    private String task_name;
    private int task_id;
    private String tool_task_id;
}
