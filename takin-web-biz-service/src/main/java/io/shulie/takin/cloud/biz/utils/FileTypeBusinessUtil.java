package io.shulie.takin.cloud.biz.utils;

import io.shulie.takin.cloud.common.enums.scenemanage.FileTypeEnum;

/**
 * 文件类型 业务工具类
 * 配合 @see FileTypeEnum
 *
 * @author liuchuan
 * @date 2021/4/25 9:46 上午
 */
public class FileTypeBusinessUtil {

    /**
     * 文件是脚本类型, 或是数据类型
     *
     * @param fileType 文件类型
     * @return 是否
     */
    public static boolean isScriptOrData(Integer fileType) {
        return isScript(fileType) || isData(fileType);
    }

    /**
     * 文件是脚本类型
     *
     * @param fileType 文件类型
     * @return 是否
     */
    public static boolean isScript(Integer fileType) {
        return FileTypeEnum.SCRIPT.getCode().equals(fileType);
    }

    /**
     * 文件是数据类型
     *
     * @param fileType 文件类型
     * @return 是否
     */
    public static boolean isData(Integer fileType) {
        return FileTypeEnum.DATA.getCode().equals(fileType);
    }

    /**
     * 文件是附件类型
     *
     * @param fileType 文件类型
     * @return 是否
     */
    public static boolean isAttachment(Integer fileType) {
        return FileTypeEnum.ATTACHMENT.getCode().equals(fileType);
    }

}
