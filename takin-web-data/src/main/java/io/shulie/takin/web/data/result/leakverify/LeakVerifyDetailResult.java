package io.shulie.takin.web.data.result.leakverify;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/6 4:23 下午
 */
@Data
public class LeakVerifyDetailResult {
    private Long id;
    /**
     * 验证结果id
     */
    private Long resultId;

    /**
     * 漏数sql
     */
    private String leakSql;

    /**
     * 是否漏数 0:正常;1:漏数;2:未检测;3:检测失败
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
