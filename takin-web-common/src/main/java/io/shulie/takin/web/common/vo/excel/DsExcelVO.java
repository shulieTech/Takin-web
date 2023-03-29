package io.shulie.takin.web.common.vo.excel;

import java.io.Serializable;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * 数据源导出配置
 * @author mubai
 * @date 2021-02-24 16:16
 */

@Data
public class DsExcelVO  implements Serializable {
    private static final long serialVersionUID = -8089242784938560042L;

    @ExcelProperty(value ="dbType",index = 0)
    private Byte dbType;

    @ExcelProperty(value ="dsType",index = 1)
    private Byte dsType;

    @ExcelProperty(value ="url",index = 2)
    private String url;

    @ExcelProperty(value ="config",index = 3)
    private String config ;

    @ExcelProperty(value ="parseConfig",index = 4)
    private String parseConfig ;

    @ExcelProperty(value ="status",index = 5)
    private Byte status;

}
