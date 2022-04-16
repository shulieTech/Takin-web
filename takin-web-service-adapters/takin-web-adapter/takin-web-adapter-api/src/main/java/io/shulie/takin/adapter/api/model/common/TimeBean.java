package io.shulie.takin.adapter.api.model.common;

import java.util.concurrent.TimeUnit;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/17 下午9:51
 */
@Data
public class TimeBean {

    private Long time;

    private String unit;

    public TimeBean() {

    }

    public TimeBean(Long time, String unit) {
        this.time = time;
        this.unit = unit;
    }

    /**
     * 返回单位为秒的时间
     */
    public long getSecondTime() {
        TimeUnitEnum e = TimeUnitEnum.value(unit);
        long t = this.getTime() == null ? 0 : this.getTime();
        if (null != e) {
            t = TimeUnit.SECONDS.convert(t, e.getUnit());
        }
        return t;
    }
}
