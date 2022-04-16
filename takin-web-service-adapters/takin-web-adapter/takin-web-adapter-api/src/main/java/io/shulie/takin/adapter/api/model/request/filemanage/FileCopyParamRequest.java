package io.shulie.takin.adapter.api.model.request.filemanage;

import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileCopyParamRequest {

    /**
     * 目标文件路径
     */
    private String targetPath;

    /**
     * 原文件路径
     */
    private List<String> sourcePaths;
}
