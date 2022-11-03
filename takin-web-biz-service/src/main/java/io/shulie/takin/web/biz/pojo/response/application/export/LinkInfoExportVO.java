package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 链路配置导出-链路概览
 */
@Data
public class LinkInfoExportVO {

    @ColumnWidth(20)
    @ExcelProperty(value ="链路名称",index = 0)
    private String linkName;

    @ExcelProperty(value ="业务环节名称",index = 1)
    private String entranceName;

    @ColumnWidth(20)
    @ExcelProperty(value ="请求入口url",index = 2)
    private String entranceUrl;

    @ColumnWidth(25)
    @ExcelProperty(value ="应用名称",index = 3)
    private String applicationNames;
}
