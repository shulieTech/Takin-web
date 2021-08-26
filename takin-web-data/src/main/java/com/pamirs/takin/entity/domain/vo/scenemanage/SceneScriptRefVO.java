package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/17 下午5:26
 */
@Data
public class SceneScriptRefVO implements Serializable {

    private static final long serialVersionUID = -2991318843153108331L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "上传ID")
    private String uploadId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "上传时间")
    private String uploadTime;

    @ApiModelProperty(value = "上传路径")
    private String uploadPath;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "上传数据量")
    private Long uploadedData;

    @ApiModelProperty(value = "是否拆分")
    private Integer isSplit;

    @ApiModelProperty(value = "Topic")
    private String topic;

    @ApiModelProperty(value = "文件类型")
    private Integer fileType;
}
