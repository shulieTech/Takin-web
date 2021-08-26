package com.pamirs.takin.entity.domain.vo.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/8 22:36
 */
@Data
@ApiModel(value = "middleWareEntity", description = "中间件实体类")
public class MiddleWareEntity {
    @ApiModelProperty(name = "id", value = "中间件类型id")
    private Long id;

    @ApiModelProperty(name = "middleWareType", value = "中间件类型")
    private String middleWareType;

    @ApiModelProperty(name = "middleWareName", value = "中间件名称")
    private String middleWareName;

    @ApiModelProperty(name = "version", value = "中间件版本号")
    private String version;

    /**
     * jar名字
     */
    private String jarName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof MiddleWareEntity) {
            MiddleWareEntity t = (MiddleWareEntity)o;
            return this.middleWareName.equalsIgnoreCase(t.middleWareName)
                && this.version.equalsIgnoreCase(t.version)

                && this.middleWareType.equalsIgnoreCase(t.middleWareType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return
            +this.middleWareType.hashCode()
                + this.middleWareName.hashCode()
                + this.version.hashCode();
    }

}
