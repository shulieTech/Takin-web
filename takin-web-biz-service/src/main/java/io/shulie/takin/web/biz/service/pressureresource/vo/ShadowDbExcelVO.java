package io.shulie.takin.web.biz.service.pressureresource.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShadowDbExcelVO  implements Serializable {
    @ColumnWidth(50)
    @ExcelProperty(value = {"Takin为您生成了影子库账号和密码（如不用系统生成的账号或密码，请替换新的账号密码到此表中", "业务数据源地址"}, index = 0)
    private String businessDatabase;

    @ColumnWidth(25)
    @ExcelProperty(value = {"Takin为您生成了影子库账号和密码（如不用系统生成的账号或密码，请替换新的账号密码到此表中", "隔离方案"}, index = 1)
    private String isolateType;

    @ColumnWidth(50)
    @ExcelProperty(value = {"Takin为您生成了影子库账号和密码（如不用系统生成的账号或密码，请替换新的账号密码到此表中", "影子数据源地址（请提供）"}, index = 2)
    private String shadowDatabase;

    @ColumnWidth(25)
    @ExcelProperty(value = {"Takin为您生成了影子库账号和密码（如不用系统生成的账号或密码，请替换新的账号密码到此表中", "影子库账号"}, index = 3)
    private String shadowUsername;

    @ColumnWidth(25)
    @ExcelProperty(value = {"Takin为您生成了影子库账号和密码（如不用系统生成的账号或密码，请替换新的账号密码到此表中", "影子库密码"}, index = 4)
    private String shadowPassword;
}
