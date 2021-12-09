package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;
import java.util.List;

import com.pamirs.takin.entity.domain.entity.TAlarm;
import com.pamirs.takin.entity.domain.entity.TReport;

/**
 * 说明: 压测报告详情实体
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月17日
 * @since 2018/05/08
 */
public class TReportDetail implements Serializable {

    private static final long serialVersionUID = 3415195393695125154L;
    /**
     * 测试背景
     */
    private TReport tReport;

    /**
     * 告警情况
     */
    private List<TAlarm> tAlarms;

    //是否通过
    private Boolean pass;

    //报告结果
    private List<TReportResult> tReportResults;

    /**
     * 2018年5月17日
     *
     * @return the tReport
     * @author shulie
     * @version 1.0
     */
    public TReport gettReport() {
        return tReport;
    }

    /**
     * 2018年5月17日
     *
     * @param tReport the tReport to set
     * @author shulie
     * @version 1.0
     */
    public void settReport(TReport tReport) {
        this.tReport = tReport;
    }

    /**
     * 2018年5月17日
     *
     * @return the pass
     * @author shulie
     * @version 1.0
     */
    public Boolean getPass() {
        return pass;
    }

    /**
     * 2018年5月17日
     *
     * @param pass the pass to set
     * @author shulie
     * @version 1.0
     */
    public void setPass(Boolean pass) {
        this.pass = pass;
    }

    /**
     * 2018年5月17日
     *
     * @return the tAlarms
     * @author shulie
     * @version 1.0
     */
    public List<TAlarm> gettAlarms() {
        return tAlarms;
    }

    /**
     * 2018年5月17日
     *
     * @param tAlarms the tAlarms to set
     * @author shulie
     * @version 1.0
     */
    public void settAlarms(List<TAlarm> tAlarms) {
        this.tAlarms = tAlarms;
    }

    public List<TReportResult> gettReportResults() {
        return tReportResults;
    }

    public void settReportResults(List<TReportResult> tReportResults) {
        this.tReportResults = tReportResults;
    }
}
