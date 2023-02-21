package io.shulie.takin.web.common.vo.excel;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;


import lombok.Data;

/**
 * @author mubai
 * @date 2021-02-24 19:24
 */

@Data
public class ShadowJobExcelVO  implements Serializable {
    private static final long serialVersionUID = -4797605438752120965L;

    @ExcelProperty(value ="name",index = 0)
    private String name;

    @ExcelProperty(value ="type",index = 1)
    private Integer type;

    @ExcelProperty(value ="configCode",index = 2)
    private String configCode;

    @ExcelProperty(value ="status",index = 3)
    private Integer status;

    @ExcelProperty(value ="active",index = 4)
    private Integer active;

    @ExcelProperty(value ="remark",index = 5)
    private String remark;

}
