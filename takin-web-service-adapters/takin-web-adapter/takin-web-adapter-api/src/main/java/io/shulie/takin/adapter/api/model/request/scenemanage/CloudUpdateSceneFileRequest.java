package io.shulie.takin.adapter.api.model.request.scenemanage;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.UploadFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuchuan
 * @date 2021/4/25 10:16 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("请求类-更新场景对应的脚本文件")
public class CloudUpdateSceneFileRequest extends ContextExt {

    /**
     * 脚本发布id, 弃用
     */
    @Deprecated
    @ApiModelProperty(hidden = true)
    private Long scriptId;
    /**
     * 新的脚本发布id
     */
    @ApiModelProperty("新的脚本发布id")
    @NotNull(message = "新的脚本发布id不能为空")
    private Long newScriptId;
    /**
     * 旧的脚本发布id
     */
    @ApiModelProperty("旧的脚本发布id")
    @NotNull(message = "旧的脚本发布id不能为空")
    private Long oldScriptId;
    /**
     * 脚本类型
     */
    @ApiModelProperty("脚本类型")
    @NotNull(message = "脚本类型不能为空")
    private Integer scriptType;
    /**
     * 上传文件
     */
    @ApiModelProperty("上传文件")
    @NotEmpty(message = "上传文件不能为空")
    private List<UploadFileDTO> uploadFiles;

    @ApiModelProperty("是否覆盖大文件 1=覆盖 0=不覆盖")
    private Integer ifCoverBigFile;

}
