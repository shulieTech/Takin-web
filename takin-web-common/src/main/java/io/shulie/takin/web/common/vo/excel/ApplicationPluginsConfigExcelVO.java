package io.shulie.takin.web.common.vo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author caijianying
 */
@Data
public class ApplicationPluginsConfigExcelVO implements Serializable {
    private static final long serialVersionUID = 295989495644985767L;

    /**
     * 配置项
     */
    @ExcelProperty(value ="configItem")
    private String configItem;

    /**
     * 配置值
     */
    @ExcelProperty(value = "configValue")
    private String configValue;
}
