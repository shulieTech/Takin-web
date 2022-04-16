package io.shulie.takin.adapter.api.model.request.filemanage;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileCreateByStringParamRequest {

    /**
     * 文件路径
     */
    @NotNull
    private String filePath;

    /**
     * 文件内容
     */
    @NotNull
    private String fileContent;
}
