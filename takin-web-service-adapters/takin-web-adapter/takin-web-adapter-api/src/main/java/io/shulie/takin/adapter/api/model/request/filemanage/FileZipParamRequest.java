package io.shulie.takin.adapter.api.model.request.filemanage;

import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileZipParamRequest {

    /**
     * 目标文件路径
     */
    private String targetPath;

    /**
     * 原文件路径
     */
    private List<String> sourcePaths;

    /**
     * 最终生成zip文件名称
     */
    private String zipFileName;

    /**
     * 是否覆盖目标文件，false 如果目标文件已存在，直接返回目标文件；true 覆盖目标文件
     */
    private Boolean isCovered;
}
