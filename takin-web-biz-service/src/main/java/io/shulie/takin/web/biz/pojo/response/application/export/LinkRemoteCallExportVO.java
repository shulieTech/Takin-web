package io.shulie.takin.web.biz.pojo.response.application.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 链路配置导出-远程调用
 */
@Data
public class LinkRemoteCallExportVO {

    @ExcelProperty(value ="请求入口url",index = 0)
    private String entranceUrl;

    @ExcelProperty(value ="调用接口名称",index = 1)
    private String remoteCallApiName;

    @ExcelProperty(value ="调用方",index = 2)
    private String sourceName;

    @ExcelProperty(value ="被调用方",index = 3)
    private String targetName;

    @ExcelProperty(value ="类型",index = 4)
    private String type;

    @ExcelProperty(value ="是否放行",index = 5)
    private String isRelease;

    @ExcelProperty(value ="mock值",index = 6)
    private String mockValue;
}
