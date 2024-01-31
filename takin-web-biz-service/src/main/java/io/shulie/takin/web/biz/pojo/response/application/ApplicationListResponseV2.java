package io.shulie.takin.web.biz.pojo.response.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author liuchuan
 * @date 2021/12/8 4:02 下午
 */
@Data
public class ApplicationListResponseV2 {

    /**
     * 不是表id
     * 防止前端long类型精度缺失, 使用字符串
     */
    @ApiModelProperty("应用id")
    private String id;

    @ApiModelProperty("接入状态； 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常 ")
    private Integer accessStatus;

    @ApiModelProperty("更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("负责人")
    private String userName;

    @ApiModelProperty("负责人CN")
    private String nickName;

    @ApiModelProperty("应用名称")
    private String applicationName;

}
