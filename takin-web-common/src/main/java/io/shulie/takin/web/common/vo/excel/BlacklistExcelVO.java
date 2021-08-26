package io.shulie.takin.web.common.vo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author mubai
 * @date 2021-03-01 10:13
 */

@Data
public class BlacklistExcelVO implements Serializable {
    private static final long serialVersionUID = 295989495644985767L;

    /**
     * redisKey
     */
    @ExcelProperty(value ="redisKey",index = 0)
    private String redisKey ;

    /**
     * 是否可用(状态)
     */
    @ExcelProperty(value ="status",index = 1)
    private Integer useYn;

}
