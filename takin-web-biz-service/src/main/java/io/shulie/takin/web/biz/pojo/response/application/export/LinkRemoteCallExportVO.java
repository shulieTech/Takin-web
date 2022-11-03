package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 链路配置导出-远程调用
 */
@Data
public class LinkRemoteCallExportVO {

    @ColumnWidth(20)
    @ExcelProperty(value ="请求入口url",index = 0)
    private String entranceUrl;

    @ColumnWidth(20)
    @ExcelProperty(value ="调用接口名称",index = 1)
    private String remoteCallApiName;

    @ColumnWidth(10)
    @ExcelProperty(value ="调用方",index = 2)
    private String sourceName;

    @ColumnWidth(10)
    @ExcelProperty(value ="被调用方",index = 3)
    private String targetName;

    @ExcelProperty(value ="类型",index = 4)
    private String type;

    @ExcelProperty(value ="是否放行",index = 5)
    private String isRelease;

    @ExcelProperty(value ="mock值",index = 6)
    private String mockValue;
}
