package io.shulie.takin.web.common.vo.excel;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class ApplicationRemoteCallConfigExcelVO implements Serializable {
    private static final long serialVersionUID = 295989495644985767L;

    /**
     * 接口名称
     */
    @ExcelProperty(value ="interfaceName",index = 0)
    private String interfaceName;

    /**
     * 接口类型
     */
    @ExcelProperty(value ="interfaceType",index = 1)
    private Integer interfaceType;

    /**
     * 服务端应用名
     */
    @ExcelProperty(value ="serverAppName",index = 2)
    private String serverAppName;


    /**
     * 配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock
     */
    @ExcelProperty(value ="type",index = 3)
    private Integer type;


    /**
     * mock返回值
     */
    @ExcelProperty(value ="mockReturnValue" ,index = 4)
    private String mockReturnValue;

}
