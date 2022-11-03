package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 链路配置导出-数据库
 */
@Data
public class LinkDbExportVO {

    @ColumnWidth(20)
    @ExcelProperty(value ="请求入口url",index = 0)
    private String entranceUrl;

    @ColumnWidth(20)
    @ExcelProperty(value ="业务数据源地址",index = 1)
    private String bizDbAddress;

    @ExcelProperty(value ="中间件类型",index = 2)
    private String middlewareType;

    @ExcelProperty(value ="隔离方案",index = 3)
    private String quarantineMethod;

    @ColumnWidth(25)
    @ExcelProperty(value ="应用",index = 4)
    private String applications;

    @ExcelProperty(value ="业务库",index = 5)
    private String dbName;

    @ExcelProperty(value ="业务表",index = 6)
    private String dsName;

    @ExcelProperty(value ="类型",index = 7)
    private String type;

    @ExcelProperty(value ="是否加入影子表",index = 8)
    private String isAddShadowDs;
}
