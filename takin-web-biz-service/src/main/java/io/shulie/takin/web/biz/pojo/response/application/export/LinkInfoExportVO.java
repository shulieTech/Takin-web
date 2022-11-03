package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 链路配置导出-链路概览
 */
@Data
public class LinkInfoExportVO {

    @ExcelProperty(value ="链路名称",index = 0)
    private String linkName;

    @ExcelProperty(value ="业务环节名称",index = 1)
    private String entranceName;

    @ExcelProperty(value ="请求入口url",index = 2)
    private String entranceUrl;

    @ExcelProperty(value ="应用名称",index = 3)
    private String applicationNames;
}
