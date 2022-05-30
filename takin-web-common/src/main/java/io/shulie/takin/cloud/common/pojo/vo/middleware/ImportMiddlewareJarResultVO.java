package io.shulie.takin.cloud.common.pojo.vo.middleware;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 中间件导入结果类
 *
 * @author liuchuan
 * @date 2021/6/1 4:22 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ImportMiddlewareJarResultVO extends ImportMiddlewareJarVO {

    @Excel(name = "备注信息", orderNum = "7")
    private String remark;

}
