package io.shulie.takin.adapter.api.model.request.file;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO
 *
 * @author 张天赐
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteTempRequest extends ContextExt {
    @ApiModelProperty(value = "上传文件ID")
    private String uploadId;

    @ApiModelProperty(value = "Topic")
    private String topic;
}
