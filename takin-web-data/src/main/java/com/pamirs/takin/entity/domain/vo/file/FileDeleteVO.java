package com.pamirs.takin.entity.domain.vo.file;

import java.io.Serializable;

import io.shulie.takin.web.common.domain.WebRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/4/20 上午11:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "文件删除入参")
public class FileDeleteVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = 2147406912228264446L;

    @ApiModelProperty(value = "上传文件ID")
    private String uploadId;

    @ApiModelProperty(value = "Topic")
    private String topic;
}
