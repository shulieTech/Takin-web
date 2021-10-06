package com.pamirs.takin.entity.domain.query;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.TAlarm;

/**
 * 说明：告警查询实体类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月17日
 */
public class TAlarmQuery extends Query<TAlarm> {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    //告警开始日期
    private String beginAlarmDate;
    //告警结束日期
    private String endAlarmDate;
    //war包名称
    private List<String> warNames;

    /**
     * 2018年5月17日
     *
     * @return the beginAlarmDate
     * @author shulie
     * @version 1.0
     */
    public String getBeginAlarmDate() {
        return beginAlarmDate;
    }

    /**
     * 2018年5月17日
     *
     * @param beginAlarmDate the beginAlarmDate to set
     * @author shulie
     * @version 1.0
     */
    public void setBeginAlarmDate(String beginAlarmDate) {
        this.beginAlarmDate = beginAlarmDate;
    }

    /**
     * 2018年5月17日
     *
     * @return the endAlarmDate
     * @author shulie
     * @version 1.0
     */
    public String getEndAlarmDate() {
        return endAlarmDate;
    }

    /**
     * 2018年5月17日
     *
     * @param endAlarmDate the endAlarmDate to set
     * @author shulie
     * @version 1.0
     */
    public void setEndAlarmDate(String endAlarmDate) {
        this.endAlarmDate = endAlarmDate;
    }

    /**
     * 2018年5月17日
     *
     * @return the warNames
     * @author shulie
     * @version 1.0
     */
    public List<String> getWarNames() {
        return warNames;
    }

    /**
     * 2018年5月17日
     *
     * @param warNames the warNames to set
     * @author shulie
     * @version 1.0
     */
    public void setWarNames(List<String> warNames) {
        this.warNames = warNames;
    }
}
