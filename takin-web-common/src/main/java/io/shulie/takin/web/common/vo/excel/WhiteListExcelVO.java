package io.shulie.takin.web.common.vo.excel;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * @author mubai
 * @date 2021-02-24 19:37
 */

@Data
public class WhiteListExcelVO implements Serializable {

    private static final long serialVersionUID = 6164987851541460315L;
    /**
     * 接口名称
     */
    @ExcelProperty(value ="interfaceName",index = 0)
    private String interfaceName;

    /**
     * 白名单类型
     */
    @ExcelProperty(value ="type",index = 1)
    private String type;

    /**
     * 字典分类
     */
    @ExcelProperty(value ="dictType",index = 2)
    private String dictType;

    /**
     * 白名单状态
     */
    @ExcelProperty(value ="status",index = 3)
    private Integer useYn;

    /**
     * 全局
     */
    @ExcelProperty(value ="isGlobal",index = 4)
    private String isGlobal;

    /**
     * 手工
     */
    @ExcelProperty(value ="isHandwork",index = 5)
    private String isHandwork;

    /**
     * 生效应用
     */
    @ExcelProperty(value ="effectAppNames",index = 6)
    private String effectAppNames;




}
