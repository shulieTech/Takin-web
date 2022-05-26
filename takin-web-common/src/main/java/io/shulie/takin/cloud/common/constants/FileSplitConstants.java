package io.shulie.takin.cloud.common.constants;

/**
 * 文件切片常量
 *
 * @author -
 */
public interface FileSplitConstants {

    /**
     * 文件类型 1 数据文件
     */
    Integer FILE_TYPE_DATA_FILE = 1;

    /**
     * 文件类型 2 附件
     */
    Integer FILE_TYPE_EXTRA_FILE = 2;

    /**
     * 需要分片
     */
    String NEED_SPLIT_KEY = "needSplit";

    /**
     * 不需要分片
     */
    String NO_NEED_SPLIT_KEY = "noNeedSplit";
}
