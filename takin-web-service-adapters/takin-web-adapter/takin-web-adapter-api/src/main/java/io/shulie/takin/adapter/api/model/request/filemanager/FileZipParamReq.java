package io.shulie.takin.adapter.api.model.request.filemanager;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileZipParamReq extends ContextExt {

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
