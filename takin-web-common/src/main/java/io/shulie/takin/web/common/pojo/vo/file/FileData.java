package io.shulie.takin.web.common.pojo.vo.file;

import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/22 6:02 下午
 */
@Data
public class FileData {
    /**
     * 每列的key值
     */
    private String columnKey;

    /**
     * 本列的行数据rows
     */
    private List<Object> rows;
}
