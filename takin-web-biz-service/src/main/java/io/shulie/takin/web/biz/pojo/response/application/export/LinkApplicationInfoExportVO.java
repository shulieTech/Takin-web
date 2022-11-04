package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Objects;

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

    @ColumnWidth(15)
    @ExcelProperty(value ="是否加入压测范围",index = 4)
    private String isAddPressureScope;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkApplicationInfoExportVO that = (LinkApplicationInfoExportVO) o;
        return Objects.equals(entranceUrl, that.entranceUrl) && Objects.equals(applicationName, that.applicationName) && Objects.equals(applicationNodeNum, that.applicationNodeNum) && Objects.equals(agentNum, that.agentNum) && Objects.equals(isAddPressureScope, that.isAddPressureScope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entranceUrl, applicationName, applicationNodeNum, agentNum, isAddPressureScope);
    }
}
