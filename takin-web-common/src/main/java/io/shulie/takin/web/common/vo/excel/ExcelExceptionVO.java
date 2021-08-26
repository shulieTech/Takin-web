package io.shulie.takin.web.common.vo.excel;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/1/7 5:35 下午
 */
@Data
public class ExcelExceptionVO {
    private String code;
    private String desc;
    private String suggestion;
    private String agentCode;

}
