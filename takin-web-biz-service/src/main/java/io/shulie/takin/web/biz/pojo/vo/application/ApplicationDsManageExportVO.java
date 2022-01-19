package io.shulie.takin.web.biz.pojo.vo.application;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 影子库/表 导出 vo
 *
 * @author loseself
 * @date 2021/3/27 6:51 下午
 **/
@Data
public class ApplicationDsManageExportVO {

    @ExcelProperty(value ="dbType",index = 0)
    private Integer dbType;

    @ExcelProperty(value ="dsType",index = 1)
    private Integer dsType;

    @ExcelProperty(value ="url",index = 2)
    private String url;

    @ExcelProperty(value ="status",index = 3)
    private Integer status;

    @ExcelProperty(value ="xml config",index = 4)
    private String config ;

    @ExcelProperty(value ="userName",index = 5)
    private String userName;

    @ExcelProperty(value ="shadowDbUrl",index = 6)
    private String shadowDbUrl ;

    @ExcelProperty(value ="shadowDbUserName",index = 7)
    private String shadowDbUserName ;

    @ExcelProperty(value ="shadowDbPassword",index = 8)
    private String shadowDbPassword ;

    @ExcelProperty(value ="shadowDbMinIdle",index = 9)
    private String shadowDbMinIdle ;

    @ExcelProperty(value ="shadowDbMaxActive",index = 10)
    private String shadowDbMaxActive ;

    @ExcelProperty(value ="connPoolName",index = 11)
    private String connPoolName;

    @ExcelProperty(value ="agentSourceType",index = 12)
    private String agentSourceType;

    @ExcelProperty(value ="dbName",index = 13)
    private String dbName;

    @ExcelProperty(value ="pwd",index = 14)
    private String pwd;

    @ExcelProperty(value ="fileExtend",index = 15)
    private String fileExtend;

    @ExcelProperty(value ="shaDowFileExtend",index = 16)
    private String shaDowFileExtend;

    @ExcelProperty(value ="configJson",index = 17)
    private String configJson;

    @ExcelProperty(value ="source",index = 18)
    private Integer source;

    @ExcelProperty(value ="type",index = 19)
    private String type;

}
