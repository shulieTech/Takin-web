package io.shulie.takin.web.biz.service.pressureresource.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShadwoTableExcelVO extends BaseRowModel implements Serializable {
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
