package io.shulie.takin.web.biz.pojo.response.application;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用中间件(ApplicationMiddleware)controller 列表响应类
 *
 * @author liuchuan
 * @date 2021-06-30 16:11:27
 */
@ApiModel("出参类-列表出参")
@Data
public class ApplicationMiddlewareListResponse {

    @ApiModelProperty("记录 id")
    private Long id;

    @ApiModelProperty("中间件项目名称")
    private String artifactId;

    @ApiModelProperty("中间件组织名称")
    private String groupId;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("中间件类型, 字符串形式")
    private String type;

    @ApiModelProperty("匹配的状态, 状态, 1已支持, 2 未支持, 3 无需支持, 4 未知, 0 无")
    private Integer status;

    @ApiModelProperty("匹配的状态, 文字描述, 未知, 未支持, 已支持, 无需支持")
    private String statusDesc = "";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("更新时间")
    private Date gmtUpdate;

}
