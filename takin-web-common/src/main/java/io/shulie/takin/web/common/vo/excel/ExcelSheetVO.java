package io.shulie.takin.web.common.vo.excel;

import java.util.List;

import lombok.Data;

/**
 * @author mubai
 * @date 2021-02-22 11:30
 */
@Data
public class ExcelSheetVO<T> {
    /**
     * sheet 名称
     */
    private String sheetName;
    /**
     * 数据列表
     */
    private List<T> data;
    /**
     * 数据类型
     */
    private Class<T> excelModelClass;
    /**
     * sheet编号
     */
    private Integer sheetNum;

}
