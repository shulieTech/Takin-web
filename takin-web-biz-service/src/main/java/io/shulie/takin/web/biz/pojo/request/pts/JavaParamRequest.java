package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author junshi
 * @ClassName KeyValueRequest
 * @Description
 * @createTime 2023年03月15日 15:39
 */
@Data
@ApiModel("JavaRequest参数格式的入参")
@NoArgsConstructor
@AllArgsConstructor
public class JavaParamRequest implements Serializable {

    @ApiModelProperty("请求中文描述")
    private String paramCNDesc;

    @ApiModelProperty("请求参数")
    private String paramName;

    @ApiModelProperty("请求值")
    private String paramValue;

    @ApiModelProperty("参数类型：text-文本 file-文件")
    private String paramType;

    @ApiModelProperty("是否必填： true-是,请求值填写到input框 false-否,请求值Tips")
    private Boolean require = false;

    @ApiModelProperty(value = "是否允许编辑：true-允许 false-不允许")
    private Boolean allowEdit = true;

    private Integer sortNum;

    public JavaParamRequest(String paramName, String paramValue) {
        this.paramName = paramName;
        this.paramValue = paramValue;
    }
}
