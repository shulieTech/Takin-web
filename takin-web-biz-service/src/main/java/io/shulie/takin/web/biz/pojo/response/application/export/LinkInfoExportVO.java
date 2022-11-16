package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Objects;

/**
 * 链路配置导出-链路概览
 */
@Data
public class LinkInfoExportVO {

    @ColumnWidth(20)
    @ExcelProperty(value ="链路名称",index = 0)
    private String linkName;

    @ColumnWidth(20)
    @ExcelProperty(value ="业务环节名称",index = 1)
    private String entranceName;

    @ColumnWidth(20)
    @ExcelProperty(value ="请求入口url",index = 2)
    private String entranceUrl;

    @ColumnWidth(25)
    @ExcelProperty(value ="应用名称",index = 3)
    private String applicationNames;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkInfoExportVO that = (LinkInfoExportVO) o;
        return Objects.equals(linkName, that.linkName) && Objects.equals(entranceName, that.entranceName) && Objects.equals(entranceUrl, that.entranceUrl) && Objects.equals(applicationNames, that.applicationNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkName, entranceName, entranceUrl, applicationNames);
    }
}
