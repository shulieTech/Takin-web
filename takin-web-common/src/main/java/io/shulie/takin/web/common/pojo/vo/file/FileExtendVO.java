package io.shulie.takin.web.common.pojo.vo.file;

import lombok.Data;

/**
 * 文件表下的扩展字段 json 接收类
 *
 * @author liuchuan
 * @date 2021/4/26 6:02 下午
 */
@Data
public class FileExtendVO {

    /**
     * 是否分隔
     */
    private Integer isSplit;

    /**
     * 是否按分区顺序分割
     */
    private Integer isOrderSplit;

    /**
     * 数据计数
     */
    private Integer dataCount;

}
