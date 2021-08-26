package io.shulie.takin.web.common.vo.excel;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * @author mubai
 * @date 2021-03-01 10:13
 */

@Data
public class ShadowConsumerExcelVO implements Serializable {
    private static final long serialVersionUID = 295989495644985767L;

    /**
     * topic
     */
    @ExcelProperty(value ="topicGroup",index = 0)
    private String topicGroup;

    /**
     * MQ类型
     */
    @ExcelProperty(value ="type",index = 1)
    private String type;

    /**
     * 是否可用
     */
    @ExcelProperty(value ="status",index = 2)
    private Integer status;

}
