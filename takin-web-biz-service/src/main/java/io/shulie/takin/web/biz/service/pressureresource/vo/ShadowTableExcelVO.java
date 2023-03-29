package io.shulie.takin.web.biz.service.pressureresource.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShadowTableExcelVO  implements Serializable {
    @ColumnWidth(50)
    @ExcelProperty(value = "业务数据源地址", index = 0)
    private String businessDatabase;

    @ColumnWidth(30)
    @ExcelProperty(value = "隔离方式", index = 1)
    private String isolateType;

    @ColumnWidth(30)
    @ExcelProperty(value = "业务库", index = 2)
    private String database;

    @ColumnWidth(30)
    @ExcelProperty(value = "业务表", index = 3)
    private String businessTable;

    @ColumnWidth(30)
    @ExcelProperty(value = "是否加入影子表（请提供）", index = 4)
    private String shadowTable;
}
