package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("CSV文件")
public class PtsCsvRequest implements Serializable {

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("是否忽略首行：true-是 false-否")
    private Boolean ingoreFirstLine;

    @ApiModelProperty("对应参数，多个用,隔开")
    private String params;

//    private String uploadStatus;
//
//    private String fileSize;
//
//    private String datalines;
//
//    private List<PtsCsvLineRequest> lineParams = new ArrayList<>();
}
