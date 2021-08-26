package io.shulie.takin.web.biz.pojo.request.file;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author liuchuan
 * @date 2021/5/12 10:04 上午
 */
@Data
@ApiModel("入参类 --> 上传文件入参类")
public class FileUploadRequest {

    @ApiModelProperty("指定上传文件夹名称, 建议英文, 默认 common")
    private String folder = "common";

    @ApiModelProperty(value = "上传的文件", required = true)
    @NotNull(message = "文件" + AppConstants.MUST_BE_NOT_NULL)
    private MultipartFile file;

}
