package io.shulie.takin.web.biz.pojo.request.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "BusinessFlowDataFileV2Request", description = "业务流程数据文件入参")
public class BusinessFlowDataFileV2Request implements Serializable {


    @NotNull
    @ApiModelProperty(name = "scriptCsvDataSetId", value = "组件id")
    private Long scriptCsvDataSetId;


    private Integer isSplit;

    private Integer isOrderSplit;

}
