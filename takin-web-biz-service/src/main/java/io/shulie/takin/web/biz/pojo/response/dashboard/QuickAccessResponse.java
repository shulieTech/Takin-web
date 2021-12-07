package io.shulie.takin.web.biz.pojo.response.dashboard;

import java.util.Objects;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "快捷入口")
public class QuickAccessResponse extends UserCommonExt {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "入口名称")
    private String quickName;
    @ApiModelProperty(value = "logo地址")
    private String quickLogo;
    @ApiModelProperty(value = "接口地址")
    private String urlAddress;
    @ApiModelProperty(value = "顺序")
    private Integer order;

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        QuickAccessResponse that = (QuickAccessResponse)o;
        return quickName.equals(that.quickName);
    }

    @Override
    public String toString() {
        return "QuickAccessResponse{" +
            "quickName='" + quickName + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(quickName);
    }
}
