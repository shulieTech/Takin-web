package io.shulie.takin.web.common.vo.excel;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * @author mubai
 * @date 2021-02-24 17:47
 */
@Data
public class LinkGuardExcelVO implements Serializable {
    private static final long serialVersionUID = -3835886136855903115L;

    @ExcelProperty(value ="methodInfo",index = 0)
    private String methodInfo;

    @ExcelProperty(value ="groovy",index = 1)
    private String groovy;

    @ExcelProperty(value ="isEnable",index = 2)
    private Boolean isEnable;

    @ExcelProperty(value ="remark",index = 3)
    private String remark;



}
