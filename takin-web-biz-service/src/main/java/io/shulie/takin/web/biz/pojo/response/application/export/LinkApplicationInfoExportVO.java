package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 链路配置导出-应用信息
 */
@Data
public class LinkApplicationInfoExportVO {

    @ColumnWidth(20)
    @ExcelProperty(value ="请求入口url",index = 0)
    private String entranceUrl;

    @ColumnWidth(10)
    @ExcelProperty(value ="应用名称",index = 1)
    private String applicationName;

    @ExcelProperty(value ="节点数",index = 2)
    private String applicationNodeNum;

    @ExcelProperty(value ="探针数",index = 3)
    private String agentNum;

    @ExcelProperty(value ="是否加入压测范围",index = 4)
    private String isAddPressureScope;
}
