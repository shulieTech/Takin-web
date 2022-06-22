package io.shulie.takin.adapter.api.model.request.scenetask;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xr.l
 */
@Data
@ApiModel("压测启动前检查文件位点返回")
public class SceneStartCheckResp {
    @ApiModelProperty(value = "是否有未读完文件")
    private Boolean hasUnread;
    @ApiModelProperty(value = "文件信息")
    private List<FileReadInfo> fileReadInfos;

    @Data
    public static class FileReadInfo {

        @ApiModelProperty(value = "文件名")
        private String fileName;
        @ApiModelProperty(value = "文件大小")
        private String fileSize;
        @ApiModelProperty(value = "已读大小")
        private String readSize;
    }
}
