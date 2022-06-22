package io.shulie.takin.cloud.common.pojo.vo.middleware;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author liuchuan
 * @date 2021/6/1 7:49 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class CompareMiddlewareJarResultVO extends CompareMiddlewareJarVO {

    @Excel(name = "比对结果", orderNum = "4")
    private String statusDesc;

    @Excel(name = "比对备注", orderNum = "5")
    private String remark;

}
