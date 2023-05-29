package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@ApiModel("数据源")
public class PtsDataSourceRequest implements Serializable {

    @ApiModelProperty("csv文件")
    private List<PtsCsvRequest> csvs = new ArrayList<>();
}