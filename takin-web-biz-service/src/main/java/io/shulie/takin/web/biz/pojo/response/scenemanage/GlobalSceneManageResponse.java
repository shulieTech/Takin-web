package io.shulie.takin.web.biz.pojo.response.scenemanage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GlobalSceneManageResponse extends AuthQueryResponseCommonExt implements Serializable {

    @ApiModelProperty(name = "id", value = "ID")
    private Long id;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
