package io.shulie.takin.web.biz.pojo.response.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/9/6 8:58 下午
 */
@Data
public class ShadowDetailResponse implements Serializable {

    @ApiModelProperty("记录id")
    private Long id;

    @ApiModelProperty("应用id")
    private Long applicationId;

    @ApiModelProperty("中间件类型")
    private String middlewareType;

    @ApiModelProperty("影子方案")
    private Integer dsType;

    @ApiModelProperty("业务数据源")
    private String url;

    @ApiModelProperty("业务数据源名称")
    private String username;

    @ApiModelProperty("业务数据源密码")
    private String password;

    @ApiModelProperty("影子数据源额外配置")
    private List<ShadowProgrammeInfo> extInfo;


    @Data
    @ApiModel("影子方案附加详情")
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShadowProgrammeInfo{

        @ApiModelProperty("属性key")
        private String key;

        @ApiModelProperty("属性名")
        private String name;

        @ApiModelProperty("属性类型")
        private String type;

        @ApiModelProperty("属性值")
        private String value;

    }
}
