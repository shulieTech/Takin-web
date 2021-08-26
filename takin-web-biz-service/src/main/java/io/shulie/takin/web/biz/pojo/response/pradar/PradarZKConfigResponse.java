package io.shulie.takin.web.biz.pojo.response.pradar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.time.DateFormatUtils;

@Data
@ApiModel("pradar配置")
public class PradarZKConfigResponse {
    @ApiModelProperty("配置ID")
    int id;
    @ApiModelProperty("ZK路径")
    String zkPath;
    @ApiModelProperty("值类型:[String,Int,Boolean]")
    String type;
    @ApiModelProperty("值")
    String value;
    @ApiModelProperty("说明")
    String remark;
    @ApiModelProperty("创建时间")
    String createTime;
    @ApiModelProperty("更新时间")
    String modifyTime;
    @ApiModelProperty("是否可编辑")
    private Boolean canEdit = true;

    public PradarZKConfigResponse(int id, String zkPath, String type, String value, String remark, long createTime,
        long modifyTime) {
        this.id = id;
        this.zkPath = zkPath;
        this.type = type;
        this.value = value;
        this.remark = remark;
        this.createTime = DateFormatUtils.format(createTime, "yyyy-MM-dd HH:mm:ss");
        this.modifyTime = DateFormatUtils.format(modifyTime, "yyyy-MM-dd HH:mm:ss");
    }
}
